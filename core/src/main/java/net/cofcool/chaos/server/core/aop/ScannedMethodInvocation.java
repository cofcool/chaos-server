package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 代理 {@link MethodInvocation}，不支持 {@link #proceed()} 操作。
 *
 * @author CofCool
 */
public class ScannedMethodInvocation implements MethodInvocation {

    private final MethodInvocation delegate;

    public ScannedMethodInvocation(MethodInvocation invocation) {
        this.delegate = invocation;
    }

    @Override
    public Method getMethod() {
        return delegate.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return delegate.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getThis() {
        return delegate.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return delegate.getStaticPart();
    }
}
