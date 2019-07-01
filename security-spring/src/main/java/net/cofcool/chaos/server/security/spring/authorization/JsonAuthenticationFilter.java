package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

/**
 * 处理 Json 请求
 *
 * @author CofCool
 */
public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private  MappingJackson2HttpMessageConverter messageConverter;

    private Class<? extends AbstractLogin> LoginType;

    public JsonAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MappingJackson2HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public Class<? extends AbstractLogin> getLoginType() {
        if (LoginType == null) {
            LoginType = DefaultLogin.class;
        }

        return LoginType;
    }

    public void setLoginType(Class<? extends AbstractLogin> loginType) {
        LoginType = loginType;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        Object loginUser;
        try {
            /*
             * 可通过 SecurityJackson2Modules 获取 Jackson 相关配置
             * ObjectMapper mapper = new ObjectMapper();
             * ClassLoader loader = getClass().getClassLoader();
             * List<Module> modules = SecurityJackson2Modules.getModules(loader);
             * mapper.registerModules(modules);
             *
             * mapper.readValue()
             */
            loginUser = messageConverter.read(getLoginType(), new ServletServerHttpRequest(request));
        } catch (IOException | HttpMessageNotReadableException e) {
            throw new AuthenticationServiceException("cannot parse the json request", e);
        }

        JsonAuthenticationToken authRequest = new JsonAuthenticationToken((AbstractLogin) loginUser);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, JsonAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(getMessageConverter(), "messageConvert must be specified");
    }

}
