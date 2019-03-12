package net.cofcool.chaos.server.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MyExceptionResolver extends GlobalHandlerExceptionResolver implements
    InitializingBean {

    @Resource(name = "jacksonObjectMapper")
    private ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        setJacksonObjectMapper(objectMapper);
    }

}
