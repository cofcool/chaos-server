package net.cofcool.chaos.server.security.shiro.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.UserAuthorizationService;
import net.cofcool.chaos.server.core.config.WebApplicationContext;
import net.cofcool.chaos.server.security.shiro.ShiroFilter;
import net.cofcool.chaos.server.security.shiro.access.AccountCredentialsMatcher;
import net.cofcool.chaos.server.security.shiro.access.AuthRealm;
import net.cofcool.chaos.server.security.shiro.access.ExceptionAuthenticationStrategy;
import net.cofcool.chaos.server.security.shiro.access.PermissionFilter;
import net.cofcool.chaos.server.security.shiro.access.UnLoginFilter;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shiro 配置
 *
 * @author CofCool
 */
@Configuration
public class ShiroAutoConfiguration {

    /**
     * 创建 <code>shiroFilter</code>
     * @param userAuthorizationService 授权相关服务
     * @param sessionManager session 管理器
     * @param shiroCacheManager  缓存管理器
     * @return shiroFilter
     */
    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilter(UserAuthorizationService userAuthorizationService, SessionManager sessionManager, @Autowired(required = false) CacheManager shiroCacheManager, PasswordProcessor passwordProcessor) {
        List<ShiroFilter> filters = new ArrayList<>();
        filters.add(new PermissionFilter(userAuthorizationService));
        filters.add(new UnLoginFilter());

        Map<String, Filter> filterMap = new HashMap<>();
        for (ShiroFilter filter : filters) {
            filterMap.put(filter.getName(), filter);
        }

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(createSecurityManager(sessionManager, createDefaultAuthenticator(userAuthorizationService, passwordProcessor), shiroCacheManager));
        factoryBean.setLoginUrl(WebApplicationContext.getConfiguration().getAuth().getLoginUrl());
        factoryBean.setUnauthorizedUrl(WebApplicationContext.getConfiguration().getAuth().getUnauthUrl());
        factoryBean.setFilterChainDefinitions(WebApplicationContext.getConfiguration().getAuth().getUrls());

        return factoryBean;
    }

    private SecurityManager createSecurityManager(SessionManager sessionManager, Authenticator authenticator, CacheManager shiroCacheManager) {
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
     * @param userAuthorizationService 授权相关服务
     * @param passwordProcessor 密码处理
     * @return authenticator
     */
    public ModularRealmAuthenticator createDefaultAuthenticator(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor) {
        List<Realm> shiroRealms = new ArrayList<>();
        shiroRealms.add(createDefaultAuthRealm(userAuthorizationService, passwordProcessor));

        ModularRealmAuthenticator realmAuthenticator = new ModularRealmAuthenticator();
        realmAuthenticator.setAuthenticationStrategy(new ExceptionAuthenticationStrategy());
        realmAuthenticator.setRealms(shiroRealms);

        return realmAuthenticator;
    }

    private Realm createDefaultAuthRealm(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor) {
        AccountCredentialsMatcher matcher = new AccountCredentialsMatcher();
        matcher.setPasswordProcessor(passwordProcessor);

        AuthRealm realm = new AuthRealm();
        realm.setCredentialsMatcher(matcher);
        realm.setUserAuthorizationService(userAuthorizationService);

        return realm;
    }

    /**
     * 创建 {@code AuthService}
     * @param userAuthorizationService 授权相关服务
     * @return authService
     */
    @Bean
    @SuppressWarnings("unchecked")
    public AuthService authService(UserAuthorizationService userAuthorizationService) {
        ShiroAuthServiceImpl authService = new ShiroAuthServiceImpl();
        authService.setUserAuthorizationService(userAuthorizationService);

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
