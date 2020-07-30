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
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.MediaType;

/**
 * 覆盖默认的退出登录逻辑, 不会跳转到登录路径, 直接返回退出成功信息
 *
 * @author CofCool
 */
@Slf4j
public class JsonLogoutFilter extends LogoutFilter {

    public static final String FILTER_KEY = "j-logout";

    private final HttpMessageConverters messageConverter;

    public JsonLogoutFilter(HttpMessageConverters messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        try {
            getSubject(request, response).logout();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }

        WebUtils.writeObjToResponse(
            messageConverter,
            (HttpServletResponse) response,
            ConfigurationSupport
                .getConfiguration()
                .getMessageWithKey(
                    ExceptionCodeDescriptor.SERVER_OK,
                    ExceptionCodeDescriptor.SERVER_OK_DESC,
                    null
                )
            ,
            MediaType.APPLICATION_JSON
        );

        return false;
    }
}
