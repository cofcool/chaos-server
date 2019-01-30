package net.cofcool.chaos.server.demo.config;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author CofCool
 */
@SpringBootApplication(scanBasePackages = "net.cofcool.chaos")
@PropertySource("classpath:application.properties")
@EnableAdminServer
public class TestApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(TestApplicationConfig.class, args);
    }

}
