package net.cofcool.chaos.server.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.cofcool.chaos.server.core.i18n.RequestLocaleChangeInterceptor;
import net.cofcool.chaos.server.core.support.ExceptionCodeManager;
import net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Mvc 配置, 包含message converter, locale等, 应用可继承该类
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

    private ObjectMapper objectMapper;
    private ExceptionCodeManager exceptionCodeManager;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    @Autowired
    public void setExceptionCodeManager(ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ResponseBodyMessageConverter converter = new ResponseBodyMessageConverter();
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
        converter.setExceptionCodeManager(exceptionCodeManager);

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
     * 根据情况设置, Spring Boot会自动配置
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


}
