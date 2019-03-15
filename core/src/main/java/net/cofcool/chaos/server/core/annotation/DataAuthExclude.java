package net.cofcool.chaos.server.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.cofcool.chaos.server.common.security.ExcludeType;

/**
 * 排除授权类型,  带有该注解的方法会忽略指定类型的参数注入。
 *
 * <br>
 *
 * 如只在<code>method<code/>上使用, 需使用<code>@Scanned</code>标注该方法所在的类, 如果该类已使用<code>@Api</code>注解, 则需使用 {@link Api#authExclude()} 配置, 直接使用 {@link #value()} 不会生效。
 *
 * @author CofCool
 *
 * @see Api
 * @see ExcludeType
 * @see Scanned
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scanned
public @interface DataAuthExclude {

    /**
     * ExcludeType 类型
     */
    Class<? extends ExcludeType> type() default ExcludeType.SimpleExcludeType.class;

    /**
     * 需要排除的字段
     */
    String[] value() default { ExcludeType.NONE };

}
