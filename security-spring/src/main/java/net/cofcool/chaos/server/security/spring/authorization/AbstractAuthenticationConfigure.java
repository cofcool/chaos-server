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
