package net.cofcool.chaos.server.security.shiro.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.AuthUserService;
import net.cofcool.chaos.server.core.config.WebApplicationContext;
import net.cofcool.chaos.server.security.shiro.access.AccountCredentialsMatcher;
import net.cofcool.chaos.server.security.shiro.access.AuthFilter;
import net.cofcool.chaos.server.security.shiro.access.AuthRealm;
import net.cofcool.chaos.server.security.shiro.access.ExceptionAuthenticationStrategy;
import net.cofcool.chaos.server.security.shiro.access.ValidateFilter;
import net.cofcool.chaos.server.security.shiro.authorization.ShiroAuthServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shiro 配置
 *
 * @author CofCool
 */
@Configuration
public class ShiroConfig {

    public static final String DEFAULT_SESSION_ID_NAME = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;

    @Bean()
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, ValidateFilter validateFilter) {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("check", validateFilter);
        filters.put("authc", new AuthFilter());

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setFilters(filters);
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl(WebApplicationContext.getConfiguration().getAuth().getLoginUrl());
        factoryBean.setUnauthorizedUrl(WebApplicationContext.getConfiguration().getAuth().getExpiredUrl());
        factoryBean.setFilterChainDefinitions(WebApplicationContext.getConfiguration().getAuth().getUrls());

        return factoryBean;
    }

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager, @Autowired(required = false) CacheManager shiroCacheManager, Authenticator authenticator) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        if (shiroCacheManager != null) {
            securityManager.setCacheManager(shiroCacheManager);
        }
        securityManager.setSessionManager(sessionManager);
        securityManager.setAuthenticator(authenticator);

        SecurityUtils.setSecurityManager(securityManager);

        return securityManager;
    }

    @Bean
    public AuthenticationStrategy authenticationStrategy() {
        return new ExceptionAuthenticationStrategy();
    }

    @Bean
    public ValidateFilter validateFilter(AuthService authService) {
        ValidateFilter validateFilter = new ValidateFilter();
        validateFilter.setAuthService(authService);

        return validateFilter;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);

        return advisor;
    }

    @Bean
    public ModularRealmAuthenticator authenticator(AuthenticationStrategy authenticationStrategy, Realm authRealm) {
        List<Realm> realms = new ArrayList<>();
        Collections.addAll(realms, authRealm);

        ModularRealmAuthenticator realmAuthenticator = new ModularRealmAuthenticator();
        realmAuthenticator.setAuthenticationStrategy(authenticationStrategy);
        realmAuthenticator.setRealms(realms);

        return realmAuthenticator;
    }

    @Bean
    public Realm authRealm(AuthUserService authUserService, PasswordProcessor passwordProcessor) {
        AccountCredentialsMatcher matcher = new AccountCredentialsMatcher();
        matcher.setPasswordProcessor(passwordProcessor);

        AuthRealm realm = new AuthRealm();
        realm.setCredentialsMatcher(matcher);
        realm.setAuthUserService(authUserService);

        return realm;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public AuthService authService(AuthUserService authUserService) {
        ShiroAuthServiceImpl authService = new ShiroAuthServiceImpl();
        authService.setAuthUserService(authUserService);

        return authService;
    }

    @Bean
    public SessionManager sessionManager(Cookie rootCookie) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(43200000);
        sessionManager.setSessionIdCookie(rootCookie);

        return sessionManager;
    }

    @Bean
    public Cookie rootCookie() {
        Cookie cookie = new SimpleCookie(DEFAULT_SESSION_ID_NAME);
        cookie.setHttpOnly(true);
        cookie.setPath(Cookie.ROOT_PATH);

        return cookie;
    }

    @Bean
    public BeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

}
