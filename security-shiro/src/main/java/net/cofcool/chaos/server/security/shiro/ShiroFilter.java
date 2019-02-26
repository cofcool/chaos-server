package net.cofcool.chaos.server.security.shiro;

import java.util.Map;
import javax.servlet.Filter;

/**
 * 为 Filter 配置名称，参考 {@link org.apache.shiro.spring.web.ShiroFilterFactoryBean#setFilters(Map)}
 *
 *
 * @see org.apache.shiro.web.servlet.NameableFilter
 * @author CofCool
 */
public interface ShiroFilter extends Filter {

    /**
     * 名称
     */
    String getName();

}
