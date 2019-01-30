package net.cofcool.chaos.server.core.config;

import javax.servlet.Filter;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * 和Spring Boot项目冲突，优先使用SpringBootServletInitializer
 *
 * @author CofCool
 */
public class SpringWebAppInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { ChaosConfiguration.class };
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { ChaosWebConfiguration.class };
    }

    protected String[] getServletMappings() {
        return new String[] { "/*" };
    }

    protected Filter[] getServletFilters() {
        return new Filter[] { new DelegatingFilterProxy() };
    }
}
