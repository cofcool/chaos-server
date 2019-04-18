package net.cofcool.chaos.server.common.core;

import java.util.Locale;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * ExceptionCodeDescriptor 的实现, 通过 {@link MessageSource} 获取异常描述信息
 */
public class ResourceExceptionCodeDescriptor implements ExceptionCodeDescriptor {

    private MessageSource messageSource;

    public ResourceExceptionCodeDescriptor(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String resolve(String type) {
        return messageSource.getMessage(type, null, type, getLocale());
    }

    protected Locale getLocale() {
        try {
            return (Locale) WebUtils.getRequest().getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        } catch(Exception e) {
            try {
                org.springframework.web.servlet.LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(
                    WebUtils.getRequest());
                if (localeResolver != null) {
                    return localeResolver.resolveLocale(WebUtils.getRequest());
                }
            } catch (Exception ignore) {}
        }

        return Locale.getDefault();
    }



    @Override
    public String getCode(String type) {
        return type;
    }

    @Override
    public String getDescription(String type) {
        return resolve(type);
    }
}
