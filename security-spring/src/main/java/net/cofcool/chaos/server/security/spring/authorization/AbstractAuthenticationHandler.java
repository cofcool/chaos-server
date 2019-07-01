package net.cofcool.chaos.server.security.spring.authorization;

import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 针对 <code>AuthenticationHandler</code> 的基本配置项
 */
public abstract class AbstractAuthenticationHandler {

    private final ExceptionCodeManager exceptionCodeManager;

    private final MappingJackson2HttpMessageConverter messageConverter;


    public AbstractAuthenticationHandler(ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter) {
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
