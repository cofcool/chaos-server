package net.cofcool.chaos.server.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author CofCool
 * @date 2018/7/16
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaServer
public class CloudApplicationConfig {

    public static void main(String[] args) {
        SpringApplication.run(CloudApplicationConfig.class, args);
    }
}
