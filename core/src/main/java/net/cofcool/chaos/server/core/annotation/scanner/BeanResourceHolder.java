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

package net.cofcool.chaos.server.core.annotation.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 注解管理。
 * <br>
 *
 * 如果使用<code>@Scanned</code>, 可通过以下方法获取：
 *
 * {@link #getAnnotation(Class, Class)}
 * {@link #getAnnotation(Method, Class)}
 * {@link #getAnnotation(Method, Class, boolean)}。
 *
 * <br>
 *
 * 若未使用<code>@Scanned</code>, 可通过前缀为"find"的方法获取。
 *
 * @see Scanned
 * @see org.springframework.core.annotation.AnnotatedElementUtils
 *
 * @author CofCool
 */
@SuppressWarnings("rawtypes")
public final class BeanResourceHolder {

    private static final Map<Method, AnnotationHelper> CACHED_METHOD_ANNOTATIONS = new HashMap<>();
    private static final Map<Class, AnnotationHelper> CACHED_CLASS_ANNOTATIONS = new HashMap<>();

    static synchronized void cacheBeans(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
            try {
                definition.resolveBeanClass(BeanResourceHolder.class.getClassLoader());
                parseAnnotation(definition.getBeanClass());
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException("could't create scanned bean", e);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private static void parseAnnotation(Class beanClass) {
        CACHED_CLASS_ANNOTATIONS.put(
            beanClass,
            new AnnotationHelper(AnnotationUtils.getAnnotations(beanClass))
        );

        for (Method method : beanClass.getMethods()) {
            CACHED_METHOD_ANNOTATIONS.computeIfAbsent(
                method, key -> {
                    Annotation[] as = AnnotationUtils.getAnnotations(method);
                    if (as == null || as.length == 0) {
                        return null;
                    } else {
                        return new AnnotationHelper(as);
                    }
                }
            );
        }
    }

    /**
     * 解析方法标注的注解
     * @param method 方法
     * @param annotationType 注解类
     * @param <T> 注解类型
     * @return 注解
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationType) {
        return BeanUtils.applyNonnull(CACHED_METHOD_ANNOTATIONS.get(method), data -> (T)data.getAnnotation(annotationType));
    }

    /**
     * 解析类标注的注解
     * @param target 目标类
     * @param annotationType 注解类
     * @param <T> 注解类型
     * @return 注解
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class target, Class<T> annotationType) {
        return BeanUtils.applyNonnull(CACHED_CLASS_ANNOTATIONS.get(target), data -> (T)data.getAnnotation(annotationType));
    }

    /**
     * 解析方法或类标注的注解
     * @param method 方法
     * @param annotationType 注解类
     * @param allScope 是否解析方法和类（如为 true, 解析方法和声明方法的类并按照"方法 > 类"的优先级返回, 如为 false, 只解析方法的注解）
     * @param <T> 注解类型
     * @return 注解
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationType, boolean allScope) {
        return allScope ?
            BeanUtils.chooseNotNullData(getAnnotation(method.getDeclaringClass(), annotationType), getAnnotation(method, annotationType))
            : getAnnotation(method, annotationType);
    }

    /**
     * 解析方法或类标注的注解
     * @param element 目标
     * @param annotationType 注解类
     * @param allScope 是否解析方法和类（如为 true, 解析方法和声明方法的类并按照"方法 > 类"的优先级返回, 如为 false, 只解析方法的注解）
     * @param <A> 注解类型
     * @return 注解
     *
     * @see AnnotatedElementUtils#findMergedAnnotation(AnnotatedElement, Class)
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement element, Class<A> annotationType, boolean allScope) {
        A annotation = AnnotatedElementUtils.findMergedAnnotation(element, annotationType);

        if (element instanceof Method && allScope) {
            return annotation != null ? annotation : AnnotatedElementUtils
                .findMergedAnnotation(((Method) element).getDeclaringClass(), annotationType);
        }

        return annotation;
    }

    static final class AnnotationHelper<T extends Annotation> {

        private Collection<T> annotations;

        AnnotationHelper(Collection<T> annotations) {
            this.annotations = annotations;
        }

        AnnotationHelper(T[] annotations) {
            if (annotations == null) {
                this.annotations = Collections.emptyList();
            } else {
                this.annotations = Arrays.asList(annotations);
            }
        }

        Collection<T> getAnnotations() {
            return Collections.unmodifiableCollection(annotations);
        }

        T getAnnotation(Class<T> annotationType) {
            for (T t : annotations) {
                if (t.annotationType().equals(annotationType)) {
                    return t;
                }
            }

            return null;
        }

    }

}
