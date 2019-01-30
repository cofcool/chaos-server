package net.cofcool.chaos.server.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 自定义请求参数解析器。<br/>
 * <p>
 * 当请求为GET时，使用Spring默认的GET请求的参数解析器，
 * 为POST时，使用messageConvert解析器，与使用@RequestBody效果一致。
 *
 * <br>
 *
 * 如只在<code>method<code/>上使用，需使用<code>@Scanned</code>标注该方法所在的类
 *
 * @author CofCool
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scanned
public @interface MultiRequestTypes {

}
