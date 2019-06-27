package net.cofcool.chaos.server.core.i18n;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * ExceptionCodeDescriptor 的实现, 通过 {@link MessageSource} 获取异常描述信息。
 * 国际化拦截器可参考 {@linkplain RequestLocaleChangeInterceptor localeChangeInterceptor}
 *
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.LocaleResolver
 * @see SessionLocaleResolver
 */
public class ResourceExceptionCodeDescriptor implements ExceptionCodeDescriptor,
    MessageSourceAware {

    private MessageSourceAccessor messageSource;



    public ResourceExceptionCodeDescriptor() {
        this.messageSource = null;
    }

    private String resolve(String type) {
        if (messageSource == null) {
            throw new IllegalArgumentException("messageSource cannot be null");
        }
        return messageSource.getMessage(type, null, type);
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
