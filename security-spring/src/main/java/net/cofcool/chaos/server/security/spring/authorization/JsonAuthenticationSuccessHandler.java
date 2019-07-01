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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * 授权成功时调用
 *
 * @author CofCool
 */
public class JsonAuthenticationSuccessHandler extends AbstractAuthenticationHandler implements AuthenticationSuccessHandler {


    public JsonAuthenticationSuccessHandler(
        ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter) {
        super(exceptionCodeManager, messageConverter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        getMessageConverter().write(
            Message.of(
                getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
                getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC),
                authentication.getPrincipal()
            ),
            MediaType.APPLICATION_JSON,
            new ServletServerHttpResponse(response)
        );
    }

}
