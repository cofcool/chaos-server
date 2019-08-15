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

import java.util.Locale;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;

/**
 * ExceptionCodeDescriptor 的实现, 通过 {@link MessageSource} 获取异常描述信息。
 * 国际化拦截器可参考 {@linkplain RequestLocaleChangeInterceptor localeChangeInterceptor}
 * <br>
 * 注意: 当描述信息不存在且调用 {@link MessageSource#getMessage(String, Object[], Locale)}
 * 未抛出 {@link NoSuchMessageException} 异常, 则默认配置并不会生效, 是否抛出异常由
 * {@link AbstractMessageSource#setUseCodeAsDefaultMessage(boolean)} 控制
 *
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.LocaleResolver
 */
public class ResourceExceptionCodeDescriptor implements ExceptionCodeDescriptor,
    MessageSourceAware {

    private MessageSourceAccessor messageSource;

    private final MessageSource defaultMessageSource = initDefaultMessageSource();

    public ResourceExceptionCodeDescriptor() {
        this.messageSource = null;
    }

    private MessageSource initDefaultMessageSource() {
        ResourceBundleMessageSource defaultMessageSource = new ResourceBundleMessageSource();
        defaultMessageSource.setBeanClassLoader(getClass().getClassLoader());
        defaultMessageSource.setBasenames("ExceptionMessages");
        defaultMessageSource.setDefaultEncoding("utf-8");

        return defaultMessageSource;
    }

    private String resolve(String type) {
        try {
            return messageSource.getMessage(type);
        } catch (NoSuchMessageException e) {
            return defaultMessageSource.getMessage(type, null, type, LocaleContextHolder.getLocale());
        }
    }

    @Override
    public String code(String type) {
        return resolve(type);
    }

    @Override
    public String description(String type) {
        return resolve(type);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messageSource = new MessageSourceAccessor(messageSource);
    }

}
