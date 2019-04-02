package net.cofcool.chaos.server.auto.config;

import com.github.pagehelper.PageInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.Filter;
import javax.sql.DataSource;
import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.UserAuthorizationService;
import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanScannerConfigure;
import net.cofcool.chaos.server.core.aop.ApiProcessingInterceptor;
import net.cofcool.chaos.server.core.aop.LoggingInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedCompositeMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedResourceAdvisor;
import net.cofcool.chaos.server.core.support.ExceptionCodeManager;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolver;
import net.cofcool.chaos.server.security.shiro.access.AccountCredentialsMatcher;
import net.cofcool.chaos.server.security.shiro.access.AuthRealm;
import net.cofcool.chaos.server.security.shiro.access.ExceptionAuthenticationStrategy;
import net.cofcool.chaos.server.security.shiro.access.JsonAuthenticationFilter;
import net.cofcool.chaos.server.security.shiro.access.PermissionFilter;
import net.cofcool.chaos.server.security.shiro.authorization.ShiroAuthServiceImpl;
import net.cofcool.chaos.server.security.spring.authorization.SpringAuthServiceImpl;
import net.cofcool.chaos.server.security.spring.authorization.UserDetail;
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
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
     * @param messageSource messageSource
     * @return ExceptionCodeManager
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionCodeManager exceptionCodeManager(MessageSource messageSource) {
        return new ExceptionCodeManager(messageSource);
    }

    @Configuration
    @EnableConfigurationProperties(value = ChaosProperties.class)
    static class PropertiesConfiguration {

        private final ChaosProperties chaosProperties;

        PropertiesConfiguration(ChaosProperties chaosProperties) {
            this.chaosProperties = chaosProperties;
        }

        @Bean
        @ConditionalOnMissingBean
        public GlobalHandlerExceptionResolver exceptionResolver(ExceptionCodeManager exceptionCodeManager) {
            GlobalHandlerExceptionResolver ex = new GlobalHandlerExceptionResolver();
            ex.setDevelopmentMode(chaosProperties.getDevelopment().getMode());
            ex.setExceptionCodeManager(exceptionCodeManager);

            return ex;
        }

        @Bean
        @ConditionalOnMissingBean
        public LoggingInterceptor loggingInterceptor() {
            return new LoggingInterceptor();
        }

        @Bean
        @ConditionalOnMissingBean
        public ApiProcessingInterceptor apiInterceptor(AuthService authService, ExceptionCodeManager exceptionCodeManager) {
            ApiProcessingInterceptor interceptor = new ApiProcessingInterceptor();
            interceptor.setAuthService(authService);
            interceptor.setDefinedCheckedKeys(
                org.springframework.util.StringUtils.delimitedListToStringArray(
                    chaosProperties.getAuth().getCheckedKeys(),
                    ","
                )
            );
            interceptor.setVersion(chaosProperties.getDevelopment().getVersion());
            interceptor.setExceptionCodeManager(exceptionCodeManager);
            return interceptor;
        }

        @Bean
        @ConditionalOnMissingBean
        public LocaleResolver localeResolver() {
            return new SessionLocaleResolver();
        }

        @Configuration
        @ConditionalOnClass(AuthService.class)
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
            public ShiroFilterFactoryBean shiroFilter(UserAuthorizationService userAuthorizationService, SessionManager sessionManager, @Autowired(required = false) CacheManager shiroCacheManager, PasswordProcessor passwordProcessor, ExceptionCodeManager exceptionCodeManager) {
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
                factoryBean.setSecurityManager(createSecurityManager(sessionManager, createDefaultAuthenticator(userAuthorizationService, passwordProcessor, chaosProperties.getAuth(), exceptionCodeManager), shiroCacheManager));
                factoryBean.setLoginUrl(chaosProperties.getAuth().getLoginUrl());
                factoryBean.setUnauthorizedUrl(chaosProperties.getAuth().getUnauthUrl());
                factoryBean.setFilterChainDefinitions("");

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
            public ModularRealmAuthenticator createDefaultAuthenticator(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor, ChaosProperties.Auth auth, ExceptionCodeManager exceptionCodeManager) {
                List<Realm> shiroRealms = new ArrayList<>();
                shiroRealms.add(createDefaultAuthRealm(userAuthorizationService, passwordProcessor, auth, exceptionCodeManager));

                ModularRealmAuthenticator realmAuthenticator = new ModularRealmAuthenticator();
                realmAuthenticator.setAuthenticationStrategy(new ExceptionAuthenticationStrategy());
                realmAuthenticator.setRealms(shiroRealms);

                return realmAuthenticator;
            }

            private Realm createDefaultAuthRealm(UserAuthorizationService userAuthorizationService, PasswordProcessor passwordProcessor, ChaosProperties.Auth auth, ExceptionCodeManager exceptionCodeManager) {
                AccountCredentialsMatcher matcher = new AccountCredentialsMatcher();
                matcher.setPasswordProcessor(passwordProcessor);

                AuthRealm realm = new AuthRealm();
                realm.setCredentialsMatcher(matcher);
                realm.setUserAuthorizationService(userAuthorizationService);
                realm.setUsingCaptcha(auth.getUsingCaptcha());
                realm.setExceptionCodeManager(exceptionCodeManager);

                return realm;
            }

            /**
             * 创建 {@code AuthService}
             * @param userAuthorizationService 授权相关服务
             * @return authService
             */
            @Bean
            @SuppressWarnings("unchecked")
            public AuthService authService(UserAuthorizationService userAuthorizationService, ExceptionCodeManager exceptionCodeManager) {
                ShiroAuthServiceImpl authService = new ShiroAuthServiceImpl();
                authService.setUserAuthorizationService(userAuthorizationService);
                authService.setExceptionCodeManager(exceptionCodeManager);

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
        @ConditionalOnClass(UserDetail.class)
        public class SpringSecurityAutoConfiguration {

            @Bean
            public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordProcessor passwordProcessor) {
                DaoAuthenticationProvider p = new DaoAuthenticationProvider();
                p.setPasswordEncoder(new PasswordEncoderDelegate(passwordProcessor));
                p.setUserDetailsService(userDetailsService);
                return p;
            }

            @Bean
            public Filter jsonAuthenticationFilter(HttpMessageConverter httpMessageConverter, AuthenticationManager authenticationManager)
                throws Exception {
                net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter filter = new net.cofcool.chaos.server.security.spring.authorization.JsonAuthenticationFilter();
                filter.setPostOnly(true);
                filter.setMessageConverter(httpMessageConverter);
                filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(chaosProperties.getAuth().getLoginUrl(), "POST"));
                filter.setAuthenticationManager(authenticationManager);

                return filter;
            }

            @Bean
            public AuthService authService(UserAuthorizationService userAuthorizationService) {
                SpringAuthServiceImpl authService = new SpringAuthServiceImpl();
                authService.setUserAuthorizationService(userAuthorizationService);

                return authService;
            }

            class PasswordEncoderDelegate implements PasswordEncoder {

                private PasswordProcessor passwordProcessor;

                PasswordEncoderDelegate(
                    PasswordProcessor passwordProcessor) {
                    this.passwordProcessor = passwordProcessor;
                }

                @Override
                public String encode(CharSequence rawPassword) {
                    return passwordProcessor.process(rawPassword.toString());
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return passwordProcessor.doMatch(rawPassword.toString(), encodedPassword);
                }
            }
        }

        @Configuration
        @ConditionalOnClass(SqlSessionFactoryBean.class)
        public class MybatisConfig {

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
