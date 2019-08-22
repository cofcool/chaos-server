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

import static org.springframework.util.StringUtils.delimitedListToStringArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.Filter;
import javax.sql.DataSource;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanScannerConfigure;
import net.cofcool.chaos.server.core.aop.ApiProcessingInterceptor;
import net.cofcool.chaos.server.core.aop.LoggingInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedCompositeMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedResourceAdvisor;
import net.cofcool.chaos.server.core.aop.ValidateInterceptor;
import net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolver;
import net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import net.cofcool.chaos.server.security.shiro.access.AccountCredentialsMatcher;
import net.cofcool.chaos.server.security.shiro.access.AuthRealm;
import net.cofcool.chaos.server.security.shiro.access.ExceptionAuthenticationStrategy;
import net.cofcool.chaos.server.security.shiro.access.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.shiro.access.PermissionFilter;
import net.cofcool.chaos.server.security.shiro.authorization.ShiroAuthServiceImpl;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImpl;
import net.cofcool.chaos.server.security.spring.authorization.SpringDaoAuthenticationProvider;
import net.cofcool.chaos.server.security.spring.authorization.SpringUserAuthorizationService;
import net.cofcool.chaos.server.security.spring.authorization.UrlBased;
import net.cofcool.chaos.server.security.spring.config.JsonLoginConfigure;
import org.apache.ibatis.plugin.Interceptor;
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
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.Advisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 项目配置
 *
 * @author CofCool
 */
@Configuration
@ConditionalOnClass(ChaosProperties.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class ChaosAutoConfiguration implements ApplicationContextAware {

    public static final String PACKAGE_PATH = "net.cofcool.chaos";

    public static final String PROJECT_CONFIGURE_PREFIX = "chaos";

    private ApplicationContext applicationContext;

    /**
     * 扫描 {@link Scanned} 注解
     */
    @Bean
    public BeanDefinitionRegistryPostProcessor beanScannerConfigurer() {
        BeanScannerConfigure configure = new BeanScannerConfigure();

        String path = PACKAGE_PATH;

        // 从配置文件中解析 annotationPath
        String annotationPath = applicationContext.getEnvironment().getProperty("chaos.development.annotation-path");
        if (!StringUtils.isNullOrEmpty(annotationPath)) {
            path = path + "," + annotationPath;
        }
        configure.setBasePackage(path);

        return configure;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 配置AOP, 使用 {@link Scanned} 注解的类可以被代理
     *
     * @param scannedMethodInterceptors 应用创建的 {@link ScannedMethodInterceptor} 实例列表
     * @return Advisor
     *
     * @see Scanned
     */
    @Bean
    public Advisor scannedResourceAdvisor(List<ScannedMethodInterceptor> scannedMethodInterceptors) {
        return new ScannedResourceAdvisor(new ScannedCompositeMethodInterceptor(scannedMethodInterceptors));
    }


    /**
     * 配置异常描述管理器
     * @param exceptionCodeDescriptor 异常描述
     * @return ExceptionCodeManager
     *
     * @see SimpleExceptionCodeDescriptor
     * @see ResourceExceptionCodeDescriptor
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionCodeManager exceptionCodeManager(@Autowired(required = false) ExceptionCodeDescriptor exceptionCodeDescriptor) {
        return new ExceptionCodeManager(
            exceptionCodeDescriptor == null
                ? SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR : exceptionCodeDescriptor
        );
    }

    @Configuration
    @EnableConfigurationProperties(value = ChaosProperties.class)
    class PropertiesConfiguration {

        private final ChaosProperties chaosProperties;

        PropertiesConfiguration(ChaosProperties chaosProperties) {
            this.chaosProperties = chaosProperties;
        }

        @Bean
        @ConditionalOnMissingBean
        public ConfigurationSupport configurationSupport(ExceptionCodeManager exceptionCodeManager) {
            ConfigurationSupport configurationSupport = new ConfigurationSupport();
            configurationSupport.setExceptionCodeManager(exceptionCodeManager);
            configurationSupport.setDebug(chaosProperties.getDevelopment().getDebug());

            return configurationSupport;
        }

        @Bean
        @ConditionalOnMissingBean
        public GlobalHandlerExceptionResolver exceptionResolver(
            ConfigurationSupport configurationSupport, ObjectMapper objectMapper) {
            GlobalHandlerExceptionResolver ex = new GlobalHandlerExceptionResolver();
            ex.setConfiguration(configurationSupport);
            ex.setJacksonObjectMapper(objectMapper);

            return ex;
        }

        @Bean
        @ConditionalOnProperty(prefix = "chaos.development", value = "logging-enabled",
            havingValue = "true", matchIfMissing = false)
        @ConditionalOnMissingBean
        public LoggingInterceptor loggingInterceptor() {
            return new LoggingInterceptor();
        }

        @Bean
        @ConditionalOnProperty(prefix = "chaos.development", value = "validating-enabled",
            havingValue = "true", matchIfMissing = false)
        @ConditionalOnMissingBean
        public ValidateInterceptor validateInterceptor() {
            return new ValidateInterceptor();
        }

        @Bean
        @ConditionalOnProperty(prefix = "chaos.development", value = "injecting-enabled",
            havingValue = "true", matchIfMissing = false)
        @ConditionalOnMissingBean
        public ApiProcessingInterceptor apiInterceptor(ConfigurationSupport configurationSupport) {
            ApiProcessingInterceptor interceptor = new ApiProcessingInterceptor();
            interceptor.setApplicationContext(ChaosAutoConfiguration.this.applicationContext);
            interceptor.setDefinedCheckedKeys(
                delimitedListToStringArray(
                    chaosProperties.getAuth().getCheckedKeys(),
                    ","
                )
            );
            interceptor.setVersion(chaosProperties.getDevelopment().getVersion());
            interceptor.setConfiguration(configurationSupport);
            return interceptor;
        }

        @Bean
        @ConditionalOnMissingBean
        public LocaleResolver localeResolver() {
            return new SessionLocaleResolver();
        }

        @Bean
        @ConditionalOnMissingBean
        public MappingJackson2HttpMessageConverter responseBodyMessageConverter(ObjectMapper objectMapper, ConfigurationSupport configurationSupport) {
            ResponseBodyMessageConverter converter = new ResponseBodyMessageConverter();
            converter.setObjectMapper(objectMapper);
            converter.setConfiguration(configurationSupport);

            return converter;
        }


        @Configuration
        @ConditionalOnClass(ShiroFilterFactoryBean.class)
        class ShiroAutoConfiguration {

            /**
             * 创建 <code>shiroFilter</code>
             * @param userAuthorizationService 授权相关服务
             * @param sessionManager session 管理器
             * @param shiroCacheManager  缓存管理器
             * @return shiroFilter
             */
            @Bean
            @ConditionalOnMissingBean
            public ShiroFilterFactoryBean shiroFilter(UserAuthorizationService userAuthorizationService, SessionManager sessionManager, @Autowired(required = false) CacheManager shiroCacheManager, PasswordProcessor passwordProcessor, ConfigurationSupport configurationSupport) {
                Map<String, Filter> filterMap = new HashMap<>();
                filterMap.put(
                    PermissionFilter.FILTER_KEY,
                    new PermissionFilter(
                        userAuthorizationService,
                        chaosProperties.getAuth().getUnauthUrl()
                    )
                );
                filterMap.put(
                    JsonAuthenticationFilter.FILTER_KEY,
                    new JsonAuthenticationFilter(
                        chaosProperties.getAuth().getLoginUrl(),
                        chaosProperties.getAuth().getUnLoginUrl()
                    )
                );

                ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
                factoryBean.setFilters(filterMap);
                factoryBean.setSecurityManager(createSecurityManager(sessionManager, createDefaultAuthenticator(userAuthorizationService, passwordProcessor, chaosProperties.getAuth(), configurationSupport), shiroCacheManager));
                factoryBean.setLoginUrl(chaosProperties.getAuth().getLoginUrl());
                factoryBean.setUnauthorizedUrl(chaosProperties.getAuth().getUnauthUrl());
                factoryBean.setFilterChainDefinitions(chaosProperties.getAuth().shiroUrls());

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
            public ModularRealmAuthenticator createDefaultAuthenticator(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor, ChaosProperties.Auth auth, ConfigurationSupport configurationSupport) {
                List<Realm> shiroRealms = new ArrayList<>();
                shiroRealms.add(createDefaultAuthRealm(userAuthorizationService, passwordProcessor, auth, configurationSupport));

                ModularRealmAuthenticator realmAuthenticator = new ModularRealmAuthenticator();
                realmAuthenticator.setAuthenticationStrategy(new ExceptionAuthenticationStrategy());
                realmAuthenticator.setRealms(shiroRealms);

                return realmAuthenticator;
            }

            private Realm createDefaultAuthRealm(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor, ChaosProperties.Auth auth, ConfigurationSupport configurationSupport) {
                AccountCredentialsMatcher matcher = new AccountCredentialsMatcher();
                matcher.setPasswordProcessor(passwordProcessor);

                AuthRealm realm = new AuthRealm();
                realm.setCredentialsMatcher(matcher);
                realm.setUserAuthorizationService(userAuthorizationService);
                realm.setUsingCaptcha(auth.getUsingCaptcha());
                realm.setConfiguration(configurationSupport);

                return realm;
            }

            /**
             * 创建 {@code AuthService}
             * @param userAuthorizationService 授权相关服务
             * @return authService
             */
            @Bean
            @SuppressWarnings("unchecked")
            public AuthService authService(UserAuthorizationService userAuthorizationService, ConfigurationSupport configurationSupport) {
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

        @Configuration
        @ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
        @ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
        class SpringSecurityConfig {

            private AuthenticationProvider authenticationProvider;

            private MappingJackson2HttpMessageConverter messageConverter;
            private ConfigurationSupport configurationSupport;

            private SpringUserAuthorizationService userAuthorizationService;

            @Autowired
            public void setMessageConverter(MappingJackson2HttpMessageConverter messageConverter) {
                this.messageConverter = messageConverter;
            }

            @Autowired
            public void setConfiguration(ConfigurationSupport configurationSupport) {
                this.configurationSupport = configurationSupport;
            }

            @Autowired
            public void setUserAuthorizationService(
                SpringUserAuthorizationService userAuthorizationService) {
                this.userAuthorizationService = userAuthorizationService;
            }

            @Configuration
            @EnableWebSecurity
            public class SpringSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

                @Override
                protected void configure(HttpSecurity http) throws Exception {
                    Assert.notNull(authenticationProvider, "authenticationProvider must be specified");
                    Assert.notNull(messageConverter, "messageConverter must be specified");
                    Assert.notNull(configurationSupport, "configurationSupport must be specified");

                    net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter authenticationFilter = new net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter();

                    if (chaosProperties.getAuth().getCorsEnabled()) {
                        http.cors();
                    }

                    if (!chaosProperties.getAuth().getCsrfEnabled()) {
                        http.csrf().disable();
                    }

                    http
                        .authenticationProvider(authenticationProvider)
                        .rememberMe()
                        .and()
                        .authorizeRequests()
                        .antMatchers(
                            delimitedListToStringArray(chaosProperties.getAuth().springExcludeUrl(), ",")
                        ).anonymous()
                        .antMatchers("/**").authenticated()
                        .accessDecisionManager(
                            new UrlBased(getDecisionVoters(http), userAuthorizationService, true)
                        )
                        .and()
                        .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .apply(new JsonLoginConfigure<>(authenticationFilter))
                        .loginProcessingUrl(chaosProperties.getAuth().getLoginUrl())
                        .configuration(configurationSupport)
                        .messageConverter(messageConverter)
                        .filterSupportsLoginType(chaosProperties.getAuth().getLoginObjectType())
                        .unAuthUrl(chaosProperties.getAuth().getUnauthUrl())
                        .unLoginUrl(chaosProperties.getAuth().getUnLoginUrl())
                        .and()
                        .logout()
                        .logoutUrl(chaosProperties.getAuth().getLogoutUrl())
                        .permitAll()
                        .and()
                        .sessionManagement()
                        .maximumSessions(10)
                        .expiredUrl(chaosProperties.getAuth().getExpiredUrl());
                }

                private List<AccessDecisionVoter<? extends Object>> getDecisionVoters(HttpSecurity http) {
                    List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();

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
                            defaultHandler.setRoleHierarchy(context.getBean(roleHiearchyBeanNames[0], RoleHierarchy.class));
                        }
                        String[] grantedAuthorityDefaultsBeanNames = context.getBeanNamesForType(
                            GrantedAuthorityDefaults.class);
                        if (grantedAuthorityDefaultsBeanNames.length == 1) {
                            GrantedAuthorityDefaults grantedAuthorityDefaults = context.getBean(grantedAuthorityDefaultsBeanNames[0], GrantedAuthorityDefaults.class);
                            defaultHandler.setDefaultRolePrefix(grantedAuthorityDefaults.getRolePrefix());
                        }
                        String[] permissionEvaluatorBeanNames = context.getBeanNamesForType(
                            PermissionEvaluator.class);
                        if (permissionEvaluatorBeanNames.length == 1) {
                            PermissionEvaluator permissionEvaluator = context.getBean(permissionEvaluatorBeanNames[0], PermissionEvaluator.class);
                            defaultHandler.setPermissionEvaluator(permissionEvaluator);
                        }
                    }

                    return defaultHandler;
                }

            }

            @Bean
            public AuthenticationProvider authenticationProvider(PasswordProcessor passwordProcessor) {
                SpringDaoAuthenticationProvider p = new SpringDaoAuthenticationProvider();
                p.setPasswordProcessor(passwordProcessor);
                p.setUserAuthorizationService(userAuthorizationService);

                this.authenticationProvider = p;

                return p;
            }

            @Bean
            public AuthService authService() {
                return new SpringAuthServiceImpl();
            }
        }

        @Configuration
        @ConditionalOnClass(SqlSessionFactoryBean.class)
        class MybatisConfig {

            @Bean
            public org.apache.ibatis.session.Configuration configuration() {
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                configuration.setUseGeneratedKeys(true);

                return configuration;
            }

            @Bean
            public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, org.apache.ibatis.session.Configuration configuration, Interceptor[] mybatisPlugins, ApplicationContext applicationContext)
                throws IOException {
                SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
                factoryBean.setMapperLocations(
                    ResourcePatternUtils
                        .getResourcePatternResolver(applicationContext)
                        .getResources(
                            chaosProperties.getData().getXmlPath()
                        )
                );
                factoryBean.setDataSource(dataSource);
                factoryBean.setPlugins(mybatisPlugins);

                factoryBean.setConfiguration(configuration);

                return factoryBean;
            }

            @Bean
            public Interceptor[] mybatisPlugins() {
                PageInterceptor interceptor = new PageInterceptor();
                Properties properties = new Properties();
                properties.setProperty("helperDialect", "mysql");
                properties.setProperty("reasonable", "true");
                interceptor.setProperties(properties);

                return new Interceptor[] {interceptor};
            }

            @Bean
            public MapperScannerConfigurer mapperScannerConfigurer() {
                MapperScannerConfigurer configurer = new MapperScannerConfigurer();
                configurer.setBasePackage(chaosProperties.getData
                    ().getMapperPackage());
                configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");

                return configurer;
            }
        }
    }

}
