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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 代理使用 {@link Scanned} 注解的类
 *
 * @see Scanned
 * @see ScannedCompositeMethodInterceptor
 *
 * @author CofCool
 */
public class ScannedResourceAdvisor extends StaticMethodMatcherPointcutAdvisor {

    private static final long serialVersionUID = -1753690870274688785L;

    public ScannedResourceAdvisor(Advice advice) {
        super(advice);
    }

    @Override
    public boolean matches(@Nonnull Method method, @Nullable Class<?> targetClass) {
        Scanned scanned = AnnotationUtils.findAnnotation(method, Scanned.class);
        if (scanned == null && targetClass != null) {
            scanned = AnnotationUtils.findAnnotation(targetClass, Scanned.class);
        }

        return scanned != null;
    }

}
