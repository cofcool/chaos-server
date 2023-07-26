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
import net.cofcool.chaos.server.common.core.ConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ConfigurationSupport.DefaultConfigurationCustomizer;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanScannerConfigure;
import net.cofcool.chaos.server.core.aop.ApiProcessingInterceptor;
import net.cofcool.chaos.server.core.aop.LoggingInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedCompositeMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedResourceAdvisor;
import net.cofcool.chaos.server.core.aop.ValidateInterceptor;
import net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolver;
import net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import org.springframework.aop.Advisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = ChaosProperties.class)
public class ChaosConfiguration implements ApplicationContextAware {

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
        String annotationPath = applicationContext.getEnvironment()
            .getProperty("chaos.development.annotation-path");
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
     * 创建 {@link ScannedResourceAdvisor}, 使用 {@link Scanned} 注解的类可以被代理
     *
     * @return Advisor
     * @see Scanned
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor scannedResourceAdvisor() {
        return new ScannedResourceAdvisor(new ScannedCompositeMethodInterceptor());
    }

    /**
     * 创建 ScannedResourceAdvisorHelper
     *
     * @param scannedResourceAdvisor ScannedResourceAdvisor
     * @return ScannedResourceAdvisorHelper
     */
    @Bean
    public ScannedResourceAdvisor.ScannedResourceAdvisorHelper scannedResourceAdvisorHelper(
        ScannedResourceAdvisor scannedResourceAdvisor) {
        return ScannedResourceAdvisor.createHelper(scannedResourceAdvisor);
    }


    /**
     * 配置异常描述管理器
     *
     * @param exceptionCodeDescriptor 异常描述
     * @return ExceptionCodeManager
     * @see SimpleExceptionCodeDescriptor
     * @see ResourceExceptionCodeDescriptor
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionCodeManager exceptionCodeManager(
        @Autowired(required = false) ExceptionCodeDescriptor exceptionCodeDescriptor) {
        return new ExceptionCodeManager(
            exceptionCodeDescriptor == null
                ? SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR : exceptionCodeDescriptor
        );
    }


    @Bean
    @ConditionalOnMissingBean
    public ConfigurationSupport configurationSupport(
        ChaosProperties chaosProperties,
        ExceptionCodeManager exceptionCodeManager,
        @Autowired(required = false) ConfigurationCustomizer configurationCustomizer) {
        return ConfigurationSupport
            .builder()
            .exceptionCodeManager(exceptionCodeManager)
            .isDebug(chaosProperties.getDevelopment().getDebug())
            .customizer(configurationCustomizer == null ? new DefaultConfigurationCustomizer()
                : configurationCustomizer)
            .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalHandlerExceptionResolver globalHandlerExceptionResolver(
        ConfigurationSupport configurationSupport, HttpMessageConverters httpMessageConverters, ChaosProperties chaosProperties) {
        GlobalHandlerExceptionResolver ex = new GlobalHandlerExceptionResolver();
        ex.setConfiguration(configurationSupport);
        ex.setHttpMessageConverters(httpMessageConverters);
        ex.setIgnoreUnknownException(chaosProperties.getDevelopment().getIgnoreUnknownException());
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
    public ApiProcessingInterceptor apiInterceptor(ChaosProperties chaosProperties, ConfigurationSupport configurationSupport) {
        ApiProcessingInterceptor interceptor = new ApiProcessingInterceptor();
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

    /**
     * 当应用主动创建 {@link HttpMessageConverters} 时, 该 {@code bean} 不会自动注入到 {@code HttpMessageConverters} 中, 需手动注入, 如 {@code new HttpMessageConverters(customConverter, responseBodyMessageConverter)}
     *
     * @return MappingJackson2HttpMessageConverter
     */
    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter responseBodyMessageConverter(
        ObjectMapper objectMapper, ConfigurationSupport configurationSupport) {
        ResponseBodyMessageConverter converter = new ResponseBodyMessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setConfiguration(configurationSupport);

        return converter;
    }

}
