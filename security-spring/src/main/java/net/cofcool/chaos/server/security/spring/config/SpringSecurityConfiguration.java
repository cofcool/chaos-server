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

import java.util.ArrayList;
import java.util.List;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.security.AuthConfig;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.spring.authorization.JsonLogoutSuccessHandler;
import net.cofcool.chaos.server.security.spring.authorization.SpringDaoAuthenticationProvider;
import net.cofcool.chaos.server.security.spring.authorization.SpringUserAuthorizationService;
import net.cofcool.chaos.server.security.spring.authorization.UrlBased;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * Security 相关配置
 */
@SuppressWarnings("rawtypes")
public class SpringSecurityConfiguration {

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
            http.cors();
        }

        if (!authConfig.getCsrfEnabled()) {
            http.csrf().disable();
        }

        http
            .authenticationProvider(authenticationProvider(passwordProcessor))
            .rememberMe()
            .and()
            .authorizeRequests()
            .antMatchers(
                delimitedListToStringArray(authConfig.springExcludeUrl(), ",")
            ).permitAll()
            .antMatchers("/**").authenticated()
            .accessDecisionManager(
                new UrlBased(getDecisionVoters(http), userAuthorizationService, true)
            )
            .and()
            .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .apply(new JsonLoginConfigure<>(authenticationFilter))
            .loginProcessingUrl(authConfig.getLoginUrl())
            .configuration(configurationSupport)
            .messageConverter(messageConverter)
            .filterSupportsLoginType(authConfig.getLoginObjectType())
            .unAuthUrl(authConfig.getUnauthUrl())
            .unLoginUrl(authConfig.getUnLoginUrl())
            .and()
            .logout()
            .logoutUrl(authConfig.getLogoutUrl())
            .logoutSuccessHandler(new JsonLogoutSuccessHandler(configurationSupport, messageConverter))
            .permitAll()
            .and()
            .sessionManagement()
            .maximumSessions(10)
            .expiredUrl(authConfig.getExpiredUrl());

        return http;
    }

    private List<AccessDecisionVoter<? extends Object>> getDecisionVoters(HttpSecurity http) {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();

        WebExpressionVoter expressionVoter = new WebExpressionVoter();
        expressionVoter.setExpressionHandler(getExpressionHandler(http));
        decisionVoters.add(expressionVoter);

        return decisionVoters;
    }

    private SecurityExpressionHandler<FilterInvocation> getExpressionHandler(HttpSecurity http) {
        DefaultWebSecurityExpressionHandler defaultHandler = new DefaultWebSecurityExpressionHandler();
        AuthenticationTrustResolver trustResolver = http
            .getSharedObject(AuthenticationTrustResolver.class);
        if (trustResolver != null) {
            defaultHandler.setTrustResolver(trustResolver);
        }
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        if (context != null) {
            String[] roleHiearchyBeanNames = context.getBeanNamesForType(RoleHierarchy.class);
            if (roleHiearchyBeanNames.length == 1) {
                defaultHandler.setRoleHierarchy(
                    context.getBean(roleHiearchyBeanNames[0], RoleHierarchy.class));
            }
            String[] grantedAuthorityDefaultsBeanNames = context.getBeanNamesForType(
                GrantedAuthorityDefaults.class);
            if (grantedAuthorityDefaultsBeanNames.length == 1) {
                GrantedAuthorityDefaults grantedAuthorityDefaults = context.getBean(
                    grantedAuthorityDefaultsBeanNames[0], GrantedAuthorityDefaults.class);
                defaultHandler.setDefaultRolePrefix(grantedAuthorityDefaults.getRolePrefix());
            }
            String[] permissionEvaluatorBeanNames = context.getBeanNamesForType(
                PermissionEvaluator.class);
            if (permissionEvaluatorBeanNames.length == 1) {
                PermissionEvaluator permissionEvaluator = context.getBean(
                    permissionEvaluatorBeanNames[0], PermissionEvaluator.class);
                defaultHandler.setPermissionEvaluator(permissionEvaluator);
            }
        }

        return defaultHandler;
    }

    public AuthenticationProvider authenticationProvider(PasswordProcessor passwordProcessor) {
        SpringDaoAuthenticationProvider p = new SpringDaoAuthenticationProvider();
        p.setPasswordProcessor(passwordProcessor);
        p.setUserAuthorizationService(userAuthorizationService);

        return p;
    }

}
