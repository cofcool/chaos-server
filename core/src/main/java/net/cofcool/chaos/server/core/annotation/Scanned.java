package net.cofcool.chaos.server.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import net.cofcool.chaos.server.core.aop.ScannedMethodInterceptor;
import net.cofcool.chaos.server.core.aop.ScannedResourceAdvisor;

/**
 * 标记资源, 若标记, 可通过 {@link BeanResourceHolder} 管理该资源
 *
 * @author CofCool
 *
 * @see ScannedResourceAdvisor
 * @see ScannedMethodInterceptor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scanned {

}
