/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test;

import feign.Contract;
import feign.Feign;
import feign.RequestLine;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.DefaultFeignLoggerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.cloud.openfeign.Targeter.DefaultTargeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import test.FeignTest.AppConfig;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(classes = AppConfig.class)
public class FeignTest {

    @Autowired
    FeignTestService feignTestService;

    @Test
    void githubTest() {
        Assertions.assertNotNull(feignTestService.request());
    }

    @FeignClient(url = "https://github.com", name = "test")
    public interface FeignTestService {

        @RequestLine("GET /")
        String request();

    }

    @Configuration
    @EnableFeignClients
    static class AppConfig {

        @Bean
        public static FeignContext feignContext() {
            return new FeignContext();
        }

        @Bean
        public FeignLoggerFactory feignLoggerFactory() {
            return new DefaultFeignLoggerFactory(null);
        }

        @Bean
        public Feign.Builder feignBuilder(Retryer retryer) {
            return Feign.builder().retryer(retryer);
        }

        @Bean
        public Retryer feignRetryer() {
            return Retryer.NEVER_RETRY;
        }

        @Bean
        public Decoder feignDecoder() {
            return new Decoder.Default();
        }

        @Bean
        public Encoder feignEncoder() {
            return new Encoder.Default();
        }

        @Bean
        public Contract feignContract() {
            // 如果想支持 Spring Mvc 的注解, 可参考 Spring Cloud Openfeign 中的 SpringMvcContract
            return new Contract.Default();
        }

        @Bean
        public FeignClientProperties feignClientProperties() {
            return new FeignClientProperties();
        }

        @Bean
        public Targeter feignTargeter() {
            return new DefaultTargeter();
        }
    }

}
