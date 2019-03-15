package net.cofcool.chaos.server.core.aop;

import java.util.List;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/**
 * 代理配置, 可通过 {@link #setInterceptorList(List)} 方法配置 {@link ScannedMethodInterceptor}, 用户可自定义{@link ScannedMethodInterceptor} 实例, 被拦截对象需使用 {@link Scanned} 注解
 *
 * @see Scanned
 *
 * @author CofCool
 */
public class ScannedCompositeMethodInterceptor implements MethodInterceptor {

    private List<ScannedMethodInterceptor> interceptorList;

    public ScannedCompositeMethodInterceptor(List<ScannedMethodInterceptor> interceptorList) {
        setInterceptorList(interceptorList);
    }

    public void setInterceptorList(List<ScannedMethodInterceptor> interceptorList) {
        Assert.notNull(interceptorList, "interceptorList - this argument is required; it must not be null");

        AnnotationAwareOrderComparator.sort(interceptorList);

        this.interceptorList = interceptorList;
    }

    public List<ScannedMethodInterceptor> getInterceptorList() {
        return interceptorList;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;

        invoke(invocation, false, result);
        result = invocation.proceed();
        invoke(invocation, true, result);

        return result;
    }

    private void invoke(MethodInvocation invocation, boolean isAfter, Object obj) throws Throwable {
        for (ScannedMethodInterceptor interceptor : interceptorList) {
            if (interceptor.supports(invocation.getMethod())) {
                if (isAfter) {
                    interceptor.postAfter(invocation, obj);
                } else {
                    interceptor.postBefore(invocation);
                }
            }
        }
    }
}
