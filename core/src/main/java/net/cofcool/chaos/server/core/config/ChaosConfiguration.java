package net.cofcool.chaos.server.core.config;

import java.lang.reflect.Method;
import java.util.List;
import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanScannerConfigure;
import net.cofcool.chaos.server.core.aop.ScannedCompositeMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedResourceAdvisor;
import net.cofcool.chaos.server.core.support.ExceptionCodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;

/**
 * 项目配置
 *
 * @author CofCool
 */
@Configuration
@EnableAsync
@ComponentScan(basePackages = ChaosConfiguration.PACKAGE_PATH, excludeFilters = {@Filter(value = Controller.class)})
@EnableConfigurationProperties(value = ChaosProperties.class)
public class ChaosConfiguration implements AsyncConfigurer, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ChaosConfiguration.class);

    public static final String PACKAGE_PATH = "net.cofcool.chaos";

    public static final String PROJECT_CONFIGURE_PREFIX = "chaos";

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            private final Logger log = LoggerFactory.getLogger(this.getClass());

            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.error("async task exception: method={};params={};exception=", method, params, ex);
            }
        };
    }

    /**
     * 扫描 {@link Scanned} 注解
     */
    @Bean
    public BeanDefinitionRegistryPostProcessor beanScannerConfigurer() {
        BeanScannerConfigure configure = new BeanScannerConfigure();

        String path = PACKAGE_PATH;

        String annotationPath = WebApplicationContext.getConfiguration().getDevelopment().getAnnotationPath();
        if (!StringUtils.isNullOrEmpty(annotationPath)) {
            path = path + "," + annotationPath;
        }
        configure.setBasePackage(path);

        return configure;
    }

    /**
     * 配置AOP，使用 {@link Scanned} 注解的类可以被代理
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 解析配置文件
        ChaosProperties properties = Binder
            .get(applicationContext.getEnvironment())
            .bind(PROJECT_CONFIGURE_PREFIX, Bindable.of(ChaosProperties.class))
            .get();
        WebApplicationContext.setConfiguration(properties);

        // 配置 ExceptionCodeManager
        ExceptionCodeManager.setMessageSource(applicationContext.getBean(MessageSource.class));
        if (log.isTraceEnabled()) {
            log.trace("parse application configuration file successful");
        }
    }

}
