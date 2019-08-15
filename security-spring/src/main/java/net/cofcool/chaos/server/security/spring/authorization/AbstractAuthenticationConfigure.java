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

import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;

/**
 * 针对 <code>Authentication</code> 的基本配置项
 */
public abstract class AbstractAuthenticationConfigure {

    private final ExceptionCodeManager exceptionCodeManager;

    private final MappingJackson2HttpMessageConverter messageConverter;

    public AbstractAuthenticationConfigure(ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter) {
        Assert.notNull(exceptionCodeManager, "exceptionCodeManager cannot be null");
        Assert.notNull(messageConverter, "messageConverter cannot be null");
        this.exceptionCodeManager = exceptionCodeManager;
        this.messageConverter = messageConverter;
    }

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }
}
