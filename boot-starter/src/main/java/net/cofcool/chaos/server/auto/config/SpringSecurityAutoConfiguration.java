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

package net.cofcool.chaos.server.auto.config;

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImpl;
import net.cofcool.chaos.server.security.spring.authorization.SpringUserAuthorizationService;
import net.cofcool.chaos.server.security.spring.config.SpringSecurityConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = ChaosProperties.class)
@ConditionalOnClass(value = {DefaultAuthenticationEventPublisher.class, SpringUserAuthorizationService.class})
@ConditionalOnProperty(value = "enabled", havingValue = "true", prefix = "chaos.auth", matchIfMissing = true)
@EnableWebSecurity
public class SpringSecurityAutoConfiguration {


    @Bean
    public SecurityFilterChain jsonSpringSecurityFilterChain(
        ChaosProperties chaosProperties, HttpSecurity httpSecurity,
        HttpMessageConverters messageConverter, PasswordProcessor passwordProcessor,
        UserAuthorizationService userAuthorizationService,
        ConfigurationSupport configurationSupport) throws Exception {
        SpringSecurityConfiguration securityConfiguration = new SpringSecurityConfiguration(
            messageConverter, userAuthorizationService, configurationSupport, passwordProcessor,
            chaosProperties.getAuth());
        return securityConfiguration.buildFilterChain(httpSecurity);
    }

    @Bean
    public AuthService authService() {
        return new SpringAuthServiceImpl();
    }
}
