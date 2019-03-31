package net.cofcool.chaos.server.security.shiro.access;

import net.cofcool.chaos.server.common.security.authorization.UserAuthorizationService;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限处理, 未授权时跳转到 {@link #unAuthUrl}
 *
 * @author CofCool
 */
public class PermissionFilter extends AccessControlFilter {

    public static final String FILTER_KEY = "check";

    private UserAuthorizationService authorizationService;

    private String unAuthUrl;

    public String getUnAuthUrl() {
        return unAuthUrl;
    }

    public void setUnAuthUrl(String unAuthUrl) {
        this.unAuthUrl = unAuthUrl;
    }

    public PermissionFilter(UserAuthorizationService authorizationService, String unAuthUrl) {
        this.authorizationService = authorizationService;
        this.unAuthUrl = unAuthUrl;
    }

    public UserAuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(UserAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    protected boolean isAccessAllowed(javax.servlet.ServletRequest servletRequest,
                                      javax.servlet.ServletResponse servletResponse, Object o) throws Exception {
        if (!getAuthorizationService().checkPermission(servletRequest, servletResponse)) {
            servletRequest.getRequestDispatcher(getUnAuthUrl()).forward(servletRequest, servletResponse);
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
