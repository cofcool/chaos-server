/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private final Map<Method, Boolean> supportStatuses = new ConcurrentHashMap<>();

    @Override
    public boolean supports(Method method) {
        return supportStatuses.computeIfAbsent(
            method,
            m ->
                doSupport(m, m.getDeclaringClass())
        );
    }

    /**
     * 是否支持
     * @param method method
     * @param targetClass targetClass
     * @return 是否支持
     */
    protected abstract boolean doSupport(Method method, Class<?> targetClass);

    @Override
    public void postBefore(MethodInvocation invocation) throws Throwable {
        doBefore(new ScannedMethodInvocation(invocation));
    }

    @Override
    public void postAfter(MethodInvocation invocation, Object returnValue) throws Throwable {
        doAfter(new ScannedMethodInvocation(invocation), returnValue);
    }

    /**
     * invocation 调用前调用
     */
    protected void doBefore(MethodInvocation invocation) throws Throwable {

    }

    /**
     * invocation 调用后调用
     * @param invocation invocation
     * @param returnValue 方法返回值，当发生异常时为异常对象
     */
    protected void doAfter(MethodInvocation invocation, Object returnValue) throws Throwable {

    }
}
