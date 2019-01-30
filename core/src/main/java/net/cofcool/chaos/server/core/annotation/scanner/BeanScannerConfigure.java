package net.cofcool.chaos.server.core.annotation.scanner;

import static org.springframework.util.Assert.notNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 * 扫描使用 {@link Scanned} 注解的类
 *
 * @author CofCool
 */
public class BeanScannerConfigure implements BeanDefinitionRegistryPostProcessor,
    InitializingBean, ApplicationContextAware, BeanNameAware {


    private String basePackage;

    private String beanName;

    Set<BeanDefinitionHolder> existsBeanDefinitions = new HashSet<>();

    private ApplicationContext applicationContext;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
        throws BeansException {
        AnnotationDefinitionScanner scanner = new AnnotationDefinitionScanner(registry);
        scanner.addAnnotationClass(Scanned.class);
        scanner.registerFilters();

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
        throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class AnnotationDefinitionScanner extends ClassPathBeanDefinitionScanner {

        private Set<Class<? extends Annotation>> annotationClasses = new HashSet<>();

        AnnotationDefinitionScanner(
            BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        public Set<Class<? extends Annotation>> getAnnotationClasses() {
            return annotationClasses;
        }

        public void setAnnotationClasses(
            Set<Class<? extends Annotation>> annotationClasses) {
            notNull(annotationClasses, "annotationClasses is required");
            this.annotationClasses = annotationClasses;
        }

        public void addAnnotationClass(Class<? extends Annotation> annotationClass) {
            annotationClasses.add(annotationClass);
        }

        public void registerFilters() {
            for (Class<? extends Annotation> filter : annotationClasses) {
                addIncludeFilter(new AnnotationTypeFilter(filter));
            }
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

            if (logger.isDebugEnabled()) {
                logger.debug( "scan beans: " + beanDefinitions.toString());
            }

            existsBeanDefinitions.addAll(beanDefinitions);
            BeanResourceHolder.cacheBeans(existsBeanDefinitions);

            return beanDefinitions;
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition)
            throws IllegalStateException {
            if (!getRegistry().containsBeanDefinition(beanName)) {
                return true;
            } else {
                existsBeanDefinitions.add(new BeanDefinitionHolder(beanDefinition, beanName));
            }

            return super.checkCandidate(beanName, beanDefinition);
        }

        @Override
        public void clearCache() {
            existsBeanDefinitions.clear();
            super.clearCache();
        }
    }
}
