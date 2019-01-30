package net.cofcool.chaos.server.core.i18n;

import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 国际化拦截器
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
