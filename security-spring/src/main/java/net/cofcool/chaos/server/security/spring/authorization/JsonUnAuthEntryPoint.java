package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;

/**
 * 处理没有权限时的情况, "Spring Security" 默认为跳转到登录页面, 改为跳转到 <code>unAuthUrl</code>
 *
 * @see org.springframework.security.web.access.ExceptionTranslationFilter
 * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
 */
@Slf4j
public class JsonUnAuthEntryPoint extends AbstractAuthenticationConfigure implements AuthenticationEntryPoint {

    private String unAuthUrl;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    public JsonUnAuthEntryPoint(
        ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter,
        String unAuthUrl) {
        super(exceptionCodeManager, messageConverter);

        Assert.notNull(unAuthUrl, "unAuthUrl cannot be null");
        this.unAuthUrl = unAuthUrl;
    }

    public String getUnAuthUrl() {
        return unAuthUrl;
    }

    public void setUnAuthUrl(String unAuthUrl) {
        this.unAuthUrl = unAuthUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        String newUrl = unAuthUrl + "?ex=" + authException.getMessage();

        log.debug("Server side forward to: " + newUrl);

        redirectStrategy.sendRedirect(request, response, newUrl);
    }
}
