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

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * ExceptionCodeDescriptor 的实现, 通过 {@link MessageSource} 获取异常描述信息.
 * 资源文件格式为:
 * <pre>SERVER_OK=00:serverOk</pre>
 * 默认使用 {@literal :} 分割描述码和描述信息, 可通过 {@link #ResourceExceptionCodeDescriptor(String)} 定义该分隔符.
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

    private MessageSourceAccessor messageSource = null;
    private final Map<String, CodeMessage> cachedMessage = new HashMap<>();
    private final String separator;

    private final MessageSource defaultMessageSource = initDefaultMessageSource();

    /**
     * 创建实例, key-value 分隔符默认为 {@code :}
     */
    public ResourceExceptionCodeDescriptor() {
        this.separator = ":";
    }

    /**
     * 创建实例
     * @param separator key-value 分隔符
     */
    public ResourceExceptionCodeDescriptor(String separator) {
        Objects.requireNonNull(separator);
        this.separator = separator;
    }

    private MessageSource initDefaultMessageSource() {
        ResourceBundleMessageSource defaultMessageSource = new ResourceBundleMessageSource();
        defaultMessageSource.setBeanClassLoader(getClass().getClassLoader());
        defaultMessageSource.setBasenames("ExceptionMessages");
        defaultMessageSource.setDefaultEncoding("utf-8");

        return defaultMessageSource;
    }

    private CodeMessage resolve(String type) {
        return cachedMessage.computeIfAbsent(type, t -> {
            String[] strings = StringUtils.split(getMessage(t), separator);
            if (strings == null) {
                strings = new String[]{t, t};
            }
            return ExceptionCodeDescriptor.message(strings[0], strings[1]);
        });
    }

    private String getMessage(String key) {
        try {
            return messageSource.getMessage(key);
        } catch (NoSuchMessageException e) {
            return defaultMessageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
        }
    }

    @Override
    public String code(String type) {
        return resolve(type).code();
    }

    @Override
    public String description(String type) {
        return resolve(type).desc();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messageSource = new MessageSourceAccessor(messageSource);
    }

}
