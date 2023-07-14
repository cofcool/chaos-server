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

package net.cofcool.chaos.server.core.i18n;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 国际化拦截器, 扩展了 {@link LocaleChangeInterceptor}, 可根据请求头中的"Accept-Language"获取区域信息
 *
 * @author CofCool
 */
public class RequestLocaleChangeInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws ServletException {
        String newLocale = request.getParameter(getParamName());

        if (newLocale == null) {
            // SessionLocaleResolver
            // try to get locale message by Accept-Language of request-header or default locale of localeResolver

            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

            if (localeResolver == null) {
                throw new IllegalStateException(
                        "No LocaleResolver found: not in a DispatcherServlet request?");
            }

            // the web context will cache the request locale
            Locale requestLocale = localeResolver.resolveLocale(request);
            try {
                localeResolver.setLocale(request, response, requestLocale);
            } catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    logger.debug("Ignoring invalid locale value [" + requestLocale.toString() + "]: " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
        } else {
            return super.preHandle(request, response, handler);
        }

        return true;
    }
}
