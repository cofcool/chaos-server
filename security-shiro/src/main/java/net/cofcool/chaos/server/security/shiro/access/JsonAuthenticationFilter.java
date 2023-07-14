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

import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.MediaType;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理登录和未登录, 未登录时跳转到 {@link #UnLoginUrl}, 重写"Shiro"默认的未登录处理方法。
 *
 * @author CofCool
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
public class JsonAuthenticationFilter extends FormAuthenticationFilter {

    public static final String FILTER_KEY = DefaultFilter.authc.name();

    private final String UnLoginUrl;

    private final AuthService authService;
    private final Class<? extends AbstractLogin> loginType;
    private final HttpMessageConverters messageConverter;


    public String getUnLoginUrl() {
        return UnLoginUrl;
    }

    public JsonAuthenticationFilter(String loginUrl, String unLoginUrl, HttpMessageConverters messageConverter, AuthService authService, Class<? extends AbstractLogin> loginType) {
        this.UnLoginUrl = unLoginUrl;
        setLoginUrl(loginUrl);

        this.messageConverter = messageConverter;
        this.authService = authService;
        this.loginType = loginType;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response)
        throws Exception {
        try {
            AbstractLogin login = WebUtils.readObjFromRequest(messageConverter, (HttpServletRequest) request, loginType);
            Message message = authService.login((HttpServletRequest) request, (HttpServletResponse) response, login);

            log.info("login message: {}", message);
            writeMessage(message, (HttpServletResponse) response);

            return true;
        } catch (IOException e) {
            log.error("login error", e);
            writeMessage(
                ConfigurationSupport
                    .getConfiguration()
                    .getMessage(
                        ExceptionCodeDescriptor.AUTH_ERROR,
                        null
                    ),
                (HttpServletResponse) response);

            return false;
        }
    }

    protected void writeMessage(Message message, HttpServletResponse response) throws Exception {
        WebUtils.writeObjToResponse(messageConverter, response, message, MediaType.APPLICATION_JSON);
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

            String newUrl = getUnLoginUrl() + "?ex=" + ConfigurationSupport.getConfiguration().getExceptionDescription(ExceptionCodeDescriptor.DENIAL_AUTH);

            request.getRequestDispatcher(newUrl).forward(request, response);

            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
        Object mappedValue) {
        boolean isAuthenticated = super.isAccessAllowed(request, response, mappedValue);
        boolean isLoginRequest = isLoginRequest(request, response);

        // 处理重复登录
        if (isAuthenticated && isLoginRequest) {
            SecurityUtils.getSubject().logout();

            return false;
        }

        return isAuthenticated ||
            (!isLoginRequest && isPermissive(mappedValue));
    }
}
