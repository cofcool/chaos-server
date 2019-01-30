package net.cofcool.chaos.server.demo.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import net.cofcool.chaos.server.core.aop.AbstractLoggingInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author CofCool
 */
@Component
public class LogInterceptor extends AbstractLoggingInterceptor {

    @Resource(name = "jacksonObjectMapper")
    private ObjectMapper objectMapper;

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
