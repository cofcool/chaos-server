/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
