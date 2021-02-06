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
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 拦截接口, 被代理类需使用 {@link Scanned} 注解, 推荐继承 {@link AbstractScannedMethodInterceptor}
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
     * @param invocation invocation
     * @param returnValue 方法返回值，当发生异常时为异常对象
     */
    void postAfter(MethodInvocation invocation, Object returnValue) throws Throwable;

}
