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

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFailureHandler;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationSuccessHandler;
import net.cofcool.chaos.server.security.spring.authorization.JsonUnAuthEntryPoint;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 配置 {@link JsonAuthenticationFilter}, 需要 {@link ConfigurationSupport} 和 {@link MappingJackson2HttpMessageConverter}
 *
 * @author CofCool
 *
 * @see org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
 */
public final class JsonLoginConfigure<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, JsonLoginConfigure<H>, JsonAuthenticationFilter> {

    private ConfigurationSupport configuration;
    private HttpMessageConverters messageConverter;

    private String unAuthUrl = "/unauth";
    private String unLoginUrl = "/unlogin";

    private boolean usingDefaultFailureHandler = true;
    private boolean usingDefaultSuccessHandler = true;

    public JsonLoginConfigure() {
        this(new JsonAuthenticationFilter());
    }

    public JsonLoginConfigure(JsonAuthenticationFilter authenticationFilter) {
        super(authenticationFilter, "/login");
    }

    public JsonLoginConfigure<H> configuration(ConfigurationSupport configuration) {
        this.configuration = configuration;

        return this;
    }

    public JsonLoginConfigure<H> messageConverter(HttpMessageConverters messageConverter) {
        this.messageConverter = messageConverter;
        getAuthenticationFilter().setMessageConverter(messageConverter);

        return this;
    }

    private void createHandler() {
        if (usingDefaultFailureHandler) {
            this.failureHandler(new JsonAuthenticationFailureHandler(configuration, messageConverter));
        }
        if (usingDefaultSuccessHandler) {
            this.successHandler(new JsonAuthenticationSuccessHandler(configuration, messageConverter));
        }
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    public JsonLoginConfigure<H> failureHandler(JsonAuthenticationFailureHandler failureHandler) {
        super.failureHandler(failureHandler);
        this.usingDefaultFailureHandler = false;

        return this;
    }

    public JsonLoginConfigure<H> successHandler(JsonAuthenticationSuccessHandler successHandler) {
        super.successHandler(successHandler);
        this.usingDefaultSuccessHandler = false;

        return this;
    }

    public JsonLoginConfigure<H> filterSupportsLoginType(Class<? extends AbstractLogin> loginType) {
        getAuthenticationFilter().setLoginType(loginType);

        return this;
    }

    public JsonLoginConfigure<H> unAuthUrl(String unAuthUrl) {
        this.unAuthUrl = unAuthUrl;

        return this;
    }

    public JsonLoginConfigure<H> unLoginUrl(String unLoginUrl) {
        this.unLoginUrl = unLoginUrl;

        return this;
    }

    @Override
    public void init(H http) throws Exception {
        updateAuthenticationDefaults();
        updateAccessDefaults(http);

        createHandler();

        registerJsonAuthenticationEntryPoint(http, new JsonUnAuthEntryPoint(configuration, messageConverter, unAuthUrl, unLoginUrl));
    }

    @SuppressWarnings("unchecked")
    private void registerJsonAuthenticationEntryPoint(H http, AuthenticationEntryPoint authenticationEntryPoint) {
        ExceptionHandlingConfigurer<H> exceptionHandling = http
            .getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor(
            postProcess(authenticationEntryPoint), getAuthenticationEntryPointMatcher(http));
    }

}
