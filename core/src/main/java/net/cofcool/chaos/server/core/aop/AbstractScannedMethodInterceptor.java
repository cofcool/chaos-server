package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 抽象类, 实现 ScannedMethodInterceptor 接口, 推荐应用继承该类, 而不是直接实现 ScannedMethodInterceptor
 *
 * @author CofCool
 */
public abstract class AbstractScannedMethodInterceptor implements ScannedMethodInterceptor {

    private Map<Method, Boolean> supportStatuses = new ConcurrentHashMap<>();

    @Override
    public boolean supports(Method method) {
        return supportStatuses.computeIfAbsent(
            method,
            m ->
                doSupport(m, m.getDeclaringClass())
        );
    }

    protected abstract boolean doSupport(Method method, Class targetClass);

    @Override
    public void postBefore(MethodInvocation invocation) throws Throwable {
        doBefore(new ScannedMethodInvocation(invocation));
    }

    @Override
    public void postAfter(MethodInvocation invocation, Object returnValue) throws Throwable {
        doAfter(new ScannedMethodInvocation(invocation), returnValue);
    }

    /**
     * invocation调用前调用
     */
    protected void doBefore(MethodInvocation invocation) throws Throwable {

    }

    /**
     * invocation调用后调用
     */
    protected void doAfter(MethodInvocation invocation, Object returnValue) throws Throwable {

    }
}
