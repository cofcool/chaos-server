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

package net.cofcool.chaos.server.security.spring.config;

import static org.springframework.util.StringUtils.delimitedListToStringArray;

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.security.AuthConfig;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.AuthorizationException;
import net.cofcool.chaos.server.common.util.WebUtils;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.spring.authorization.JsonLogoutSuccessHandler;
import net.cofcool.chaos.server.security.spring.authorization.SpringDaoAuthenticationProvider;
import net.cofcool.chaos.server.security.spring.authorization.SpringUserAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Security 相关配置
 */
@SuppressWarnings("rawtypes")
public class SpringSecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger("");

    private final HttpMessageConverters messageConverter;

    private final ConfigurationSupport configurationSupport;

    private final PasswordProcessor passwordProcessor;

    private final SpringUserAuthorizationService userAuthorizationService;

    private final AuthConfig authConfig;


    public SpringSecurityConfiguration(HttpMessageConverters messageConverter, UserAuthorizationService userAuthorizationService, ConfigurationSupport configurationSupport, PasswordProcessor passwordProcessor, AuthConfig authConfig) {
        this.messageConverter = messageConverter;
        this.configurationSupport = configurationSupport;
        this.passwordProcessor = passwordProcessor;
        this.authConfig = authConfig;
        if (userAuthorizationService instanceof SpringUserAuthorizationService) {
            this.userAuthorizationService = (SpringUserAuthorizationService) userAuthorizationService;
        } else {
            this.userAuthorizationService = SpringUserAuthorizationService.of(userAuthorizationService);
        }
    }


    public SecurityFilterChain buildFilterChain(HttpSecurity httpSecurity) throws Exception {
        return configure(httpSecurity).build();
    }

    private HttpSecurity configure(HttpSecurity http) throws Exception {
        Assert.notNull(messageConverter, "messageConverter must be specified");
        Assert.notNull(configurationSupport, "configurationSupport must be specified");

        JsonAuthenticationFilter authenticationFilter = new JsonAuthenticationFilter();

        if (authConfig.getCorsEnabled()) {
            http.cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()));
        }

        if (!authConfig.getCsrfEnabled()) {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        http
            .authenticationProvider(authenticationProvider(passwordProcessor))
            .rememberMe(r  ->  r.useSecureCookie(true))
            .authorizeHttpRequests(r ->
                r
                    .requestMatchers(delimitedListToStringArray(authConfig.springExcludeUrl(), ",")).permitAll()
                    .requestMatchers("/**").authenticated()
                    .requestMatchers("/**").access((authentication, context) -> {
                            boolean check = true;
                            try {
                                userAuthorizationService.checkPermission(
                                    context.getRequest(),
                                    null,
                                    authentication.get(),
                                    WebUtils.getRealRequestPath(context.getRequest())
                                );
                            } catch (AuthorizationException e) {
                                LOGGER.info("userAuthorizationService.checkPermission fail", e);
                                check = false;
                            }
                            return new AuthorizationDecision(check);
                        }
                    )
            )
            .logout(l ->
                l
                    .logoutUrl(authConfig.getLogoutUrl())
                    .logoutSuccessHandler(new JsonLogoutSuccessHandler(configurationSupport, messageConverter))
                    .permitAll()
            )
            .sessionManagement(s ->
                s.maximumSessions(10).expiredUrl(authConfig.getExpiredUrl())
            )
            .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .apply(
                new JsonLoginConfigure<HttpSecurity>(authenticationFilter)
                    .loginProcessingUrl(authConfig.getLoginUrl())
                    .configuration(configurationSupport)
                    .messageConverter(messageConverter)
                    .filterSupportsLoginType(authConfig.getLoginObjectType())
                    .unAuthUrl(authConfig.getUnauthUrl())
                    .unLoginUrl(authConfig.getUnLoginUrl())
            );

        return http;
    }


    public AuthenticationProvider authenticationProvider(PasswordProcessor passwordProcessor) {
        SpringDaoAuthenticationProvider p = new SpringDaoAuthenticationProvider();
        p.setPasswordProcessor(passwordProcessor);
        p.setUserAuthorizationService(userAuthorizationService);

        return p;
    }

}
