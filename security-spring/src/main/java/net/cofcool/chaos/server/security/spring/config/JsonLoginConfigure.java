package net.cofcool.chaos.server.security.spring.config;

import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFailureHandler;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationSuccessHandler;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 配置 {@link JsonAuthenticationFilter}, 需要 {@link ExceptionCodeManager} 和 {@link MappingJackson2HttpMessageConverter}
 *
 * @author CofCool
 *
 * @see org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
 */
public final class JsonLoginConfigure<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, JsonLoginConfigure<H>, JsonAuthenticationFilter> {

    private ExceptionCodeManager exceptionCodeManager;
    private MappingJackson2HttpMessageConverter messageConverter;

    private boolean usingDefaultFailureHandler = true;
    private boolean usingDefaultSuccessHandler = true;

    public JsonLoginConfigure() {
        this(new JsonAuthenticationFilter());
    }

    public JsonLoginConfigure(JsonAuthenticationFilter authenticationFilter) {
        super(authenticationFilter, "/login");
    }

    public JsonLoginConfigure<H> exceptionCodeManager(ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;

        return this;
    }

    public JsonLoginConfigure<H> messageConverter(MappingJackson2HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
        getAuthenticationFilter().setMessageConverter(messageConverter);

        return this;
    }

    private void createHandler() {
        if (usingDefaultFailureHandler) {
            this.failureHandler(new JsonAuthenticationFailureHandler(exceptionCodeManager, messageConverter));
        }
        if (usingDefaultSuccessHandler) {
            this.successHandler(new JsonAuthenticationSuccessHandler(exceptionCodeManager, messageConverter));
        }
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    public JsonLoginConfigure<H> failureHandler(JsonAuthenticationFailureHandler failureHandler) {
        super.failureHandler(failureHandler);
        this.usingDefaultFailureHandler = false;
        return this;
    }

    public JsonLoginConfigure<H> successHandler(JsonAuthenticationSuccessHandler successHandler) {
        super.successHandler(successHandler);
        this.usingDefaultSuccessHandler = false;
        return this;
    }

    public JsonLoginConfigure<H> filterSupportsLoginType(Class<? extends AbstractLogin> loginType) {
        getAuthenticationFilter().setLoginType(loginType);

        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);

        createHandler();
    }

}
