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

package net.cofcool.chaos.server.core.support;

import jakarta.annotation.Resource;
import java.net.UnknownHostException;
import net.cofcool.chaos.server.common.core.ConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolverTest.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * {@link  GlobalHandlerExceptionResolver} 测试类
 * @author CofCool
 */
@SpringBootTest(classes = Config.class, properties = "debug=true")
@SpringBootApplication
class GlobalHandlerExceptionResolverTest {

    @Resource
    GlobalHandlerExceptionResolver exceptionResolver;

    @Test
    void resolveException() {
        Assertions.assertNotNull(exceptionResolver.resolveException(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), new NullPointerException()));
    }

    @Test
    void resolveUnknownException() {
        exceptionResolver.setIgnoreUnknownException(true);
        Assertions.assertNull(exceptionResolver.resolveException(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), new UnknownHostException()));
    }

    @Configuration
    static class Config {

        @EventListener(HttpRequestExceptionEvent.class)
        public void handlerHttpExceptionEvent(HttpRequestExceptionEvent event) {
            Assertions.assertNotNull(event.getSource());
        }

        @Bean
        public ExceptionCodeManager exceptionCodeManager(@Autowired(required = false) ExceptionCodeDescriptor exceptionCodeDescriptor) {
            return new ExceptionCodeManager(
                exceptionCodeDescriptor == null
                    ? SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR : exceptionCodeDescriptor
            );
        }

        @Bean
        public ConfigurationSupport configurationSupport(
            ExceptionCodeManager exceptionCodeManager,
            @Autowired(required = false) ConfigurationCustomizer configurationCustomizer) {
            return ConfigurationSupport
                .builder()
                .exceptionCodeManager(exceptionCodeManager)
                .isDebug(true)
                .customizer(configurationCustomizer == null ? new DefaultConfigurationCustomizer() : configurationCustomizer)
                .build();
        }

        @Bean
        public GlobalHandlerExceptionResolver globalHandlerExceptionResolver(ConfigurationSupport configurationSupport, HttpMessageConverters httpMessageConverters) {
            GlobalHandlerExceptionResolver ex = new GlobalHandlerExceptionResolver();
            ex.setConfiguration(configurationSupport);
            ex.setHttpMessageConverters(httpMessageConverters);
            ex.setMappedHandlerClasses(Object.class);

            return ex;
        }
    }
}