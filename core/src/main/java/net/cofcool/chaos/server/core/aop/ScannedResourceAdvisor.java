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

import net.cofcool.chaos.server.core.annotation.Scanned;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

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

    public ScannedResourceAdvisor(ScannedCompositeMethodInterceptor advice) {
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


    /**
     * 实现 {@link BeanPostProcessor} 接口把 {@link ScannedMethodInterceptor} 实例注入到 {@link ScannedResourceAdvisor} 中,
     * 不使用 "Spring Bean" 注入的原因是防止 {@code ScannedMethodInterceptor} 实例提早创建从而影响应用其它实例的生命周期
     */
    public final static class ScannedResourceAdvisorHelper implements BeanPostProcessor {

        private final ScannedResourceAdvisor advisor;

        private ScannedResourceAdvisorHelper(ScannedResourceAdvisor advisor) {
            this.advisor = advisor;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ScannedMethodInterceptor) {
                ((ScannedCompositeMethodInterceptor) advisor.getAdvice()).addInterceptor((ScannedMethodInterceptor) bean);
            }

            return bean;
        }

    }

    /**
     * 创建 ScannedResourceAdvisorHelper
     * @param advisor ScannedResourceAdvisor
     * @return ScannedResourceAdvisorHelper
     */
    public static ScannedResourceAdvisorHelper createHelper(ScannedResourceAdvisor advisor) {
        return new ScannedResourceAdvisorHelper(advisor);
    }


}
