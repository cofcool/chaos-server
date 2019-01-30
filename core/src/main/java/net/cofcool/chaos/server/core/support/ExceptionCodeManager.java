package net.cofcool.chaos.server.core.support;

import java.util.Locale;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

interface LocaleResolver {

    String resolve(Locale locale, String code);

    String resolve(String code);
}

/**
 * 统一错误码管理工具
 * <br>
 * 所有描述性字符串均从<b>properties</b>文件读取
 * <br>
 * 调用 <b>getInstance</b>方法获取实例
 *
 * @author CofCool
 */
public class ExceptionCodeManager {

    private static final ExceptionCodeManager MANAGER;

    static {
        MANAGER = new ExceptionCodeManager();
    }

    private LocaleResolver localeResolver;

    private String prefix;

    private ExceptionCodeManager() {
    }

    /**
     * 当ContextLoader.getCurrentWebApplicationContext()获取不到Context时手动设置
     */
    public static void setMessageSource(MessageSource messageSource) {
        if (MANAGER.localeResolver == null) {
            MANAGER.localeResolver = new InternalLocalResolver(messageSource);
        }
    }

    ExceptionCodeManager setPrefix(String prefix) {
        MANAGER.prefix = prefix;

        return MANAGER;
    }

    public static ExceptionCodeManager getInstance() {
        if (MANAGER.localeResolver != null) {
            return MANAGER;
        }

        synchronized (MANAGER) {
            if (MANAGER.localeResolver == null) {
                MessageSource source = ContextLoader.getCurrentWebApplicationContext()
                        .getBean(MessageSource.class);
                MANAGER.localeResolver = new InternalLocalResolver(source);
            }
        }

        return MANAGER;
    }


    public String get(String code) {
        return get(code, false);
    }

    String get(String code, boolean usePrefix) {
        return localeResolver.resolve(usePrefix ? prefix + code : code);
    }

}

class InternalLocalResolver implements LocaleResolver {

    private MessageSource messageSource;

    InternalLocalResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String resolve(Locale locale, String code) {
        return messageSource.getMessage(code, null, code, locale);
    }

    @Override
    public String resolve(String code) {
        return resolve(getLocale(), code);
    }

    private Locale getLocale() {
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

}