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

package net.cofcool.chaos.server.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.BeanCreationException;

/**
 * Bean相关工具类
 *
 * @author CofCool
 */
public final class BeanUtils {

    /**
     * 根据参数顺序作为优先级选择不为 {@literal null} 的值
     * @param args 参数
     * @return 不为 {@literal null} 的值
     */
    @SafeVarargs
    public static  <T> T chooseNotNullData(@Nonnull T... args) {
        int curIndex = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                curIndex = i;
            }
        }

        return args[curIndex];
    }

    /**
     * 拷贝 {@code source} 实例的属性到 {@code target} 中, 并返回
     * @param source 源对象
     * @param targetClass 目标类
     * @param <T> 目标类类型
     * @return 目标类实例
     *
     * @throws org.springframework.beans.BeansException 拷贝失败时抛出
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        try {
            T parent = targetClass.getConstructor().newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, parent);

            return parent;
        }  catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new BeanCreationException("creating failed", e);
        }
    }

    /**
     * 把data对象属性的值赋给 {@code target} 对象的值为 {@literal null} 的属性
     * @param target 目标对象
     * @param data 数据对象
     * @param <T> 类型
     */
    public static <T> void overwriteNullProperties(T target, T data) {
        List<String> nullProperties = new ArrayList<>();

        for (PropertyDescriptor descriptor : getPropertyDescriptors(target.getClass())) {
            try {
                if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null && descriptor.getReadMethod().invoke(target) != null) {
                    nullProperties.add(descriptor.getName());
                }
            } catch (IllegalAccessException | InvocationTargetException ignore) {}
        }

        org.springframework.beans.BeanUtils.copyProperties(data, target, nullProperties.toArray(new String[0]));
    }

    /**
     * Getter 方法名转为 Setter 方法名
     * @param getter Getter 方法名
     * @return Setter 方法名
     */
    @Nullable
    public static String getterToSetter(@Nonnull String getter) {
        if (getter.startsWith("get")) {
            return getter.replaceFirst("g", "s");
        } else if (getter.startsWith("is")) {
            return String.valueOf(getter.charAt(2)).toLowerCase() + getter.substring(3);
        }

        return null;
    }

    /**
     * {@code target} 非 {@literal null} 时执行 {@code function}
     */
    public static <T, R> R applyNonnull(T target, Function<T, R> function) {
        return target != null ? function.apply(target) : null;
    }

    /**
     * 检查输入值是否为 {@literal null} 或 0
     */
    public static <T> Optional<T> checkNullOrZero(T value) {
        if (value == null || "0".equals(value.toString())) {
            return Optional.empty();
        }

        return Optional.of(value);
    }

    /**
     * 把以"-"字符分割的字符串转换为小驼峰格式
     * 如: configure-dir -> configureDir
     * @param fieldName 源字符串
     * @return 转换后的字符串
     */
    public static String convertWordsWithDashToCamelCase(String fieldName) {
        StringBuilder newFieldName = new StringBuilder();

        for (char c : fieldName.toCharArray()) {
            if (c <= 90 && c >= 65) {
                newFieldName.append("-").append((char) (c + 32));
            } else {
                newFieldName.append(c);
            }
        }

        return newFieldName.toString();
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class targetClass) {
        return org.springframework.beans.BeanUtils.getPropertyDescriptors(targetClass);
    }

    public static PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) {
        return org.springframework.beans.BeanUtils.getPropertyDescriptor(targetClass, propertyName);
    }

    /**
     * 推荐使用{@link org.springframework.beans.BeanUtils}
     */
    private static final class BeanCache {

        private static final Map<Class, PropertyDescriptor[]> BEAN_PROPERTIES_CACHE = new HashMap<>();

        /**
         * 推荐使用{@link org.springframework.beans.BeanUtils#getPropertyDescriptors(Class)}
         */
        private static PropertyDescriptor[] cachedBeanInfo(Class clazz) {
            return BEAN_PROPERTIES_CACHE.computeIfAbsent(clazz, key -> {
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(key);
                    return beanInfo.getPropertyDescriptors();
                } catch (IntrospectionException ignore) {}

                return null;
            });
        }

    }


}
