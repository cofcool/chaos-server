package net.cofcool.chaos.server.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import net.cofcool.chaos.server.core.i18n.RequestLocaleChangeInterceptor;
import net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Spring Mvc 配置，包含message converter, locale等，应用可继承该类
 *
 * @author CofCool
 *
 * @see EnableWebMvc
 * @see WebMvcConfigurer
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
 * @see org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
 * @see org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
 */
public class ChaosWebConfiguration extends DelegatingWebMvcConfiguration {

    @Resource
    private ObjectMapper objectMapper;


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ResponseBodyMessageConverter converter = new ResponseBodyMessageConverter();
        converter.setObjectMapper(objectMapper);
        converters.add(converter);

        super.configureMessageConverters(converters);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        RequestLocaleChangeInterceptor localeChangeInterceptor = new RequestLocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale");

        registry.addInterceptor(localeChangeInterceptor);

        super.addInterceptors(registry);
    }

    /**
     * 根据情况设置，Spring Boot会自动配置
     *
     * @see org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
     */
    @Override
    public Validator getValidator() {
        Validator validator = super.getValidator();

        if (validator == null) {
            LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
            validatorFactoryBean.setProviderClass(HibernateValidator.class);
            validatorFactoryBean.setValidationMessageSource(getApplicationContext().getBean(MessageSource.class));

            return validatorFactoryBean;
        }


        return validator;
    }


    /**
     * setup ServletContext for WebMvcConfigurationSupport
     */
    @Component
    public static class ChaosWebConfigurationSupport implements ServletContextAware {

        private ChaosWebConfiguration chaosWebConfiguration;

        public ChaosWebConfigurationSupport(ChaosWebConfiguration chaosWebConfiguration) {
            this.chaosWebConfiguration = chaosWebConfiguration;
        }

        @Override
        public void setServletContext(@Nullable ServletContext servletContext) {
            if (chaosWebConfiguration.getServletContext() == null && servletContext != null) {
                chaosWebConfiguration.setServletContext(servletContext);
            }
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

}
