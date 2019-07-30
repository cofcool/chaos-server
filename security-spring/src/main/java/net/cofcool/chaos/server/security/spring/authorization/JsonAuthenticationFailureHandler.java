package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


/**
 * 授权失败时调用
 *
 * @author CofCool
 */
public class JsonAuthenticationFailureHandler extends AbstractAuthenticationConfigure implements AuthenticationFailureHandler {

    public JsonAuthenticationFailureHandler(
        ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter) {
        super(exceptionCodeManager, messageConverter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        Message message;
        if (exception instanceof UsernameNotFoundException) {
            message = getMessage(ExceptionCodeDescriptor.NO_LOGIN, ExceptionCodeDescriptor.NO_LOGIN_DESC);
        } else if (exception instanceof BadCredentialsException) {
            message = getMessage(ExceptionCodeDescriptor.USER_PASSWORD_ERROR, ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC);
        } else {
            message = getMessage(ExceptionCodeDescriptor.DENIAL_AUTH, ExceptionCodeDescriptor.DENIAL_AUTH_DESC);
        }

        getMessageConverter().write(
            message,
            MediaType.APPLICATION_JSON,
            new ServletServerHttpResponse(response)
        );
    }

    private Message getMessage(String codeKey, String descKey) {
        return Message.of(
            getExceptionCodeManager().getCode(codeKey),
            getExceptionCodeManager().getDescription(descKey)
        );
    }
}
