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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 代理 {@link MethodInvocation}, 不支持 {@link #proceed()} 操作。
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
