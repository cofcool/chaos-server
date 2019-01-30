package net.cofcool.chaos.server.security.shiro.access;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

/**
 * @author CofCool
 */
public class ValidateFilter extends AccessControlFilter {

    public static final String VALIDATE_FILTER_KEY = "validate";

    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected boolean isAccessAllowed(javax.servlet.ServletRequest servletRequest,
                                      javax.servlet.ServletResponse servletResponse, Object o) throws Exception {
        if (!authService.checkPermission(servletRequest, servletResponse)) {
            servletRequest.getRequestDispatcher("/auth/unauth").forward(servletRequest, servletResponse);
            return false;
        }

        return true;
    }

    @Override
    protected boolean onAccessDenied(javax.servlet.ServletRequest servletRequest,
                                     javax.servlet.ServletResponse servletResponse) {
        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return super.preHandle(request, WebUtils.setupCorsHeader((HttpServletResponse) response));
    }
}
