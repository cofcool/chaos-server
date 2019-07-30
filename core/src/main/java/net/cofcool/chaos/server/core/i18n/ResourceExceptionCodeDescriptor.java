package net.cofcool.chaos.server.core.i18n;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
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
