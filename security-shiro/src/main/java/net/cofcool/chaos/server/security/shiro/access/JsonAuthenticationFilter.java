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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

/**
 * 处理未登录情况, 未登录时跳转到 {@link #UnLoginUrl}, 重写"Shiro"默认的未登录处理方法。
 *
 * @author CofCool
 */
@Slf4j
public class JsonAuthenticationFilter extends FormAuthenticationFilter {

    public static final String FILTER_KEY = "authc";

    private String UnLoginUrl;

    public String getUnLoginUrl() {
        return UnLoginUrl;
    }

    public void setUnLoginUrl(String unLoginUrl) {
        UnLoginUrl = unLoginUrl;
    }

    public JsonAuthenticationFilter(String loginUrl, String unLoginUrl) {
        this.UnLoginUrl = unLoginUrl;
        setLoginUrl(loginUrl);
    }

    public JsonAuthenticationFilter() {
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
            request.getRequestDispatcher(getUnLoginUrl()).forward(request, response);

            return false;
        }
    }

}
