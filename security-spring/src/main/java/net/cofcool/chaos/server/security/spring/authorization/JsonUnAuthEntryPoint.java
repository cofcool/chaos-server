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

package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;

/**
 * 处理没有权限时的情况, "Spring Security" 默认为跳转到登录页面, 改为跳转到 <code>unAuthUrl</code>
 * </hr>
 * 如下示例:
 * <pre>
 * &#64;Api
 * &#64;RestController
 * &#64;RequestMapping(value = "/auth", method = {RequestMethod.POST, RequestMethod.GET})
 * public class AuthController {
 *
 *     &#64;Resource
 *     private ExceptionCodeManager exceptionCodeManager;
 *
 *     // ex 为异常描述
 *     &#64;RequestMapping("/unauth")
 *     public Message unauth(String ex) {
 *         return Message.of(
 *             exceptionCodeManager.getCode(ExceptionCodeDescriptor.AUTH_ERROR),
 *             ex
 *         );
 *     }
 *
 *     &#64;RequestMapping("/unlogin")
 *     public Message unlogin(String ex) {
 *         return Message.of(
 *             exceptionCodeManager.getCode(ExceptionCodeDescriptor.NO_LOGIN),
 *             ex
 *         );
 *     }
 *
 * }
 * </pre>
 *
 * @see org.springframework.security.web.access.ExceptionTranslationFilter
 * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
 */
@Slf4j
public class JsonUnAuthEntryPoint extends AbstractAuthenticationConfigure implements AuthenticationEntryPoint {

    private final String unAuthUrl;
    private final String unLoginUrl;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    public JsonUnAuthEntryPoint(
        ConfigurationSupport configuration,
        HttpMessageConverters messageConverter,
        String unAuthUrl,
        String unLoginUrl) {
        super(configuration, messageConverter);

        Assert.notNull(unAuthUrl, "unAuthUrl cannot be null");
        Assert.notNull(unLoginUrl, "unLoginUrl cannot be null");
        this.unAuthUrl = unAuthUrl;
        this.unLoginUrl = unLoginUrl;
    }

    public String getUnAuthUrl() {
        return unAuthUrl;
    }

    public String getUnLoginUrl() {
        return unLoginUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        String newUrl;
        if (authException instanceof InsufficientAuthenticationException) {
            newUrl = getUnAuthUrl() + "?ex=" + authException.getMessage();
        } else {
            newUrl = getUnLoginUrl() + "?ex=" + authException.getMessage();
        }

        log.debug("Server side forward to " + newUrl, authException);

        redirectStrategy.sendRedirect(request, response, newUrl);
    }
}
