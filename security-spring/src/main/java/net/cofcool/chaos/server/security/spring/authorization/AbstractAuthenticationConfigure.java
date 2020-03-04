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
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * 针对 <code>Authentication</code> 的基本配置项
 */
public abstract class AbstractAuthenticationConfigure {

    private final ConfigurationSupport configuration;

    private final MappingJackson2HttpMessageConverter messageConverter;

    public AbstractAuthenticationConfigure(ConfigurationSupport configuration,
        MappingJackson2HttpMessageConverter messageConverter) {
        Assert.notNull(configuration, "configuration cannot be null");
        Assert.notNull(messageConverter, "messageConverter cannot be null");
        this.configuration = configuration;
        this.messageConverter = messageConverter;
    }

    protected ConfigurationSupport getConfiguration() {
        return configuration;
    }

    protected HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    @SuppressWarnings("unchecked")
    protected void writeMessageToResponse(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication == null ? null : authentication.getPrincipal();
        getMessageConverter().write(
            getConfiguration().getMessageWithKey(
                ExceptionCodeDescriptor.SERVER_OK,
                ExceptionCodeDescriptor.SERVER_OK_DESC,
                principal
            ),
            MediaType.APPLICATION_JSON,
            new ServletServerHttpResponse(response)
        );
    }
}
