package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.Method;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 拦截接口，被代理类需使用 {@link Scanned} 注解，推荐继承 {@link AbstractScannedMethodInterceptor}
 *
 * @see Scanned
 * @see AbstractScannedMethodInterceptor
 *
 * @author CofCool
 */
public interface ScannedMethodInterceptor {

    /**
     * 是否可以处理该 method
     */
    boolean supports(Method method);

    /**
     * MethodInvocation 调用之前调用
     */
    void postBefore(MethodInvocation invocation) throws Throwable;

    /**
     * MethodInvocation 调用之后调用
     */
    void postAfter(MethodInvocation invocation, Object returnValue) throws Throwable;

}
