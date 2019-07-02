package net.cofcool.chaos.server.security.shiro.access;

import javax.servlet.http.HttpServletRequest;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

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
        try {
            getAuthorizationService()
                .checkPermission(
                    servletRequest,
                    servletResponse,
                    getSubject(servletRequest, servletResponse),
                    WebUtils.getRealRequestPath((HttpServletRequest) servletRequest)
                );
        } catch (Exception e) {
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

}
