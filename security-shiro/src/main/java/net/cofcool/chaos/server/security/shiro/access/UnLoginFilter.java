package net.cofcool.chaos.server.security.shiro.access;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.util.WebUtils;
import net.cofcool.chaos.server.core.config.ChaosProperties.Auth;
import net.cofcool.chaos.server.core.config.WebApplicationContext;
import net.cofcool.chaos.server.security.shiro.ShiroFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

/**
 * 处理未登录情况，未登录时跳转到 {@link Auth#getUnLoginUrl()}，重写"Shiro"默认的未登录处理方法。
 *
 * @author CofCool
 */
@Slf4j
public class UnLoginFilter extends FormAuthenticationFilter implements ShiroFilter {

    public static final String FILTER_KEY = "authc";

    public UnLoginFilter() {
        setLoginUrl(WebApplicationContext.getConfiguration().getAuth().getLoginUrl());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }
            request.getRequestDispatcher(WebApplicationContext.getConfiguration().getAuth().getUnLoginUrl()).forward(request, response);

            return false;
        }
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return super.preHandle(request, WebUtils.setupCorsHeader((HttpServletResponse) response));
    }

    @Override
    public String getName() {
        return FILTER_KEY;
    }
}
