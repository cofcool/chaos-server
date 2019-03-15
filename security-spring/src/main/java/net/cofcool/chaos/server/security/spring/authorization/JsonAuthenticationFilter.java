package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.AuthConstant;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * @author CofCool
 */
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private HttpMessageConverter messageConverter;

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(HttpMessageConverter  messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        DefaultLogin loginUser;
        try {
            loginUser = (DefaultLogin) messageConverter.read(DefaultLogin.class, new ServletServerHttpRequest(request));
        } catch (IOException e) {
            loginUser = new DefaultLogin("", "");
        }

        JsonAuthenticationToken authRequest = new JsonAuthenticationToken(
                loginUser.getUsername(), loginUser.getPassword());

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        getRememberMeServices().loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        // 直接返回数据, 如重定向到“/”路径可调用父类方法
        request.getSession(true).setAttribute(AuthConstant.LOGINED_USER_KEY, authResult.getPrincipal());
         messageConverter.write(authResult.getPrincipal(), MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString(), failed);
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
            logger.debug("Delegating to authentication failure handler " + getFailureHandler());
        }

        getRememberMeServices().loginFail(request, response);

        String errorCode;
        if (failed instanceof UsernameNotFoundException) {
            errorCode = ExceptionCode.NO_LOGIN;
        } else {
            errorCode = ExceptionCode.DENIAL_AUTH;
        }

        messageConverter.write(Message.error(errorCode, failed.getMessage()), MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(getMessageConverter(), "messageConvert must be specified");
    }

}
