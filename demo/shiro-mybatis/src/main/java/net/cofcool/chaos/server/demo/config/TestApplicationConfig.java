package net.cofcool.chaos.server.demo.config;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author CofCool
 */
@SpringBootApplication(
    scanBasePackages = "net.cofcool.chaos",
    exclude = {RedisAutoConfiguration.class, ErrorMvcAutoConfiguration.class}
)
@PropertySource("classpath:application.properties")
@EnableAdminServer
public class TestApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(TestApplicationConfig.class, args);
    }

}
