package net.cofcool.chaos.server.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 设定接口版本号，版本号一致时接口才会允许调用，值需为int类型，类似build_version<br/>
 *
 * <ul>
 *     <li>如果值为<b>ALLOW_ALL</b>，则适用于所有版本</li>
 *     <li>如果值为<b>DENIAL_ALL</b>，则禁止访问</li>
 * </ul>
 *
 * 可通过<b>ChaosConfiguration.CURRENT_BUILD_VERSION</b>获取当前构建版本<br/>
 *
 *
 * <br>
 *
 * 如只在<code>method<code/>上使用，需使用<code>@Scanned</code>标注该方法所在的类
 *
 *
 * @author CofCool
 *
 * @see Api
 * @see Scanned
 */
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scanned
public @interface ApiVersion {

    /**
     * 允许所有
     */
    int ALLOW_ALL = 0;

    /**
     * 拒绝所有
     */
    int DENIAL_ALL = -1;

    /**
     * 不限制
     */
    int NO_LIMIT = -2;

    int value() default ALLOW_ALL;

    int max() default NO_LIMIT;

    int min() default NO_LIMIT;

}
