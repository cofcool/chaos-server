package net.cofcool.chaos.server.demo.config;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CofCool
 */
@Configuration
public class AppConfig {

    @Bean
    public ExceptionCodeDescriptor exceptionCodeDescriptor() {
        return new MyExceptionCodeDescriptor();
    }


}
