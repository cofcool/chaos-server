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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.security.AuthConfig;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.security.shiro.access.AccountCredentialsMatcher;
import net.cofcool.chaos.server.security.shiro.access.AuthRealm;
import net.cofcool.chaos.server.security.shiro.access.ExceptionAuthenticationStrategy;
import net.cofcool.chaos.server.security.shiro.access.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.shiro.access.JsonLogoutFilter;
import net.cofcool.chaos.server.security.shiro.access.PermissionFilter;
import net.cofcool.chaos.server.security.shiro.authorization.ShiroAuthServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ShiroFilterFactoryBean.class)
@EnableConfigurationProperties(value = ChaosProperties.class)
public class ShiroAutoConfiguration {

    /**
     * 创建 <code>shiroFilter</code>
     *
     * @param userAuthorizationService 授权相关服务
     * @param sessionManager           session 管理器
     * @param shiroCacheManager        缓存管理器
     * @return shiroFilter
     */
    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilter(
        ChaosProperties chaosProperties, AuthService authService,
        UserAuthorizationService userAuthorizationService, SessionManager sessionManager,
        @Autowired(required = false) CacheManager shiroCacheManager,
        PasswordProcessor passwordProcessor, ConfigurationSupport configurationSupport,
        HttpMessageConverters httpMessageConverters) {
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put(
            PermissionFilter.FILTER_KEY,
            new PermissionFilter(
                userAuthorizationService,
                chaosProperties.getAuth().getUnauthUrl()
            )
        );
        filterMap.put(
            JsonLogoutFilter.FILTER_KEY,
            new JsonLogoutFilter(httpMessageConverters)
        );
        filterMap.put(
            JsonAuthenticationFilter.FILTER_KEY,
            new JsonAuthenticationFilter(
                chaosProperties.getAuth().getLoginUrl(),
                chaosProperties.getAuth().getUnLoginUrl(),
                httpMessageConverters,
                authService,
                chaosProperties.getAuth().getLoginObjectType()
            )
        );

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(createSecurityManager(sessionManager,
            createDefaultAuthenticator(userAuthorizationService, passwordProcessor,
                chaosProperties.getAuth(), configurationSupport), shiroCacheManager));
        factoryBean.setLoginUrl(chaosProperties.getAuth().getLoginUrl());
        factoryBean.setUnauthorizedUrl(chaosProperties.getAuth().getUnauthUrl());
        factoryBean.setFilterChainDefinitions(chaosProperties.getAuth().shiroUrls());

        return factoryBean;
    }

    private SecurityManager createSecurityManager(SessionManager sessionManager,
        Authenticator authenticator, CacheManager shiroCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        if (shiroCacheManager != null) {
            securityManager.setCacheManager(shiroCacheManager);
        }
        securityManager.setSessionManager(sessionManager);
        securityManager.setAuthenticator(authenticator);

        SecurityUtils.setSecurityManager(securityManager);

        return securityManager;
    }

    /**
     * 创建 authenticator
     *
     * @param userAuthorizationService 授权相关服务
     * @param passwordProcessor        密码处理
     * @return authenticator
     */
    public ModularRealmAuthenticator createDefaultAuthenticator(
        UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor,
        AuthConfig auth, ConfigurationSupport configurationSupport) {
        List<Realm> shiroRealms = new ArrayList<>();
        shiroRealms.add(
            createDefaultAuthRealm(userAuthorizationService, passwordProcessor, auth,
                configurationSupport));

        ModularRealmAuthenticator realmAuthenticator = new ModularRealmAuthenticator();
        realmAuthenticator.setAuthenticationStrategy(new ExceptionAuthenticationStrategy());
        realmAuthenticator.setRealms(shiroRealms);

        return realmAuthenticator;
    }

    private Realm createDefaultAuthRealm(UserAuthorizationService userAuthorizationService,
        PasswordProcessor passwordProcessor, AuthConfig auth,
        ConfigurationSupport configurationSupport) {
        AccountCredentialsMatcher matcher = new AccountCredentialsMatcher();
        matcher.setPasswordProcessor(passwordProcessor);

        AuthRealm realm = new AuthRealm();
        realm.setCredentialsMatcher(matcher);
        realm.setUserAuthorizationService(userAuthorizationService);
        realm.setConfiguration(configurationSupport);

        return realm;
    }

    /**
     * 创建 {@code AuthService}
     *
     * @param userAuthorizationService 授权相关服务
     * @return authService
     */
    @Bean
    public AuthService authService(UserAuthorizationService userAuthorizationService,
        ConfigurationSupport configurationSupport) {
        ShiroAuthServiceImpl authService = new ShiroAuthServiceImpl();
        authService.setUserAuthorizationService(userAuthorizationService);
        authService.setConfiguration(configurationSupport);

        return authService;
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionManager sessionManager() {
        return new DefaultWebSessionManager();
    }

    @Bean
    public BeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
