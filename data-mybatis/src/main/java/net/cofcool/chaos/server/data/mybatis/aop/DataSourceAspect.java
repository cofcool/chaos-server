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

package net.cofcool.chaos.server.data.mybatis.aop;

import java.lang.reflect.Method;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.data.mybatis.datasource.DataSourceType;
import net.cofcool.chaos.server.data.mybatis.datasource.DynamicDataSourceHolder;
import net.cofcool.chaos.server.data.mybatis.transaction.DynamicManagedTransaction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换切面
 **/
public class DataSourceAspect {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected void detectDataSource(JoinPoint point) throws Throwable {
        Class<?> target = point.getTarget().getClass();
        MethodSignature signature = (MethodSignature) point.getSignature();

        if (!resolveDataSource(target, signature.getMethod())) {
            for (Class<?> clazz : target.getInterfaces()) {
                if (resolveDataSource(clazz, signature.getMethod())) {
                    return;
                }
            }
        }
    }

    protected void clearDataSource(Object returnValue) {
        setupDataSource(null);
    }

    /**
     * 提取目标对象方法注解和类型注解中的数据源标识
     */
    private boolean resolveDataSource(Class<?> clazz, Method method) {
        DataSourceType dataSource = null;
        try {
            Class<?>[] types = method.getParameterTypes();
            Method m = clazz.getMethod(method.getName(), types);

            dataSource = BeanUtils.chooseNotNullData(
                    clazz.getAnnotation(DataSourceType.class),
                    m.getAnnotation(DataSourceType.class)
            );

            setupDataSource(dataSource);
        } catch (Exception e) {
            log.error("resolve datasource error: {}", e);
        }

        return dataSource != null;
    }

    private void setupDataSource(DataSourceType source) {
        if (source != null) {
            DynamicManagedTransaction.cancelConnection();
            DynamicDataSourceHolder.setDataSource(source.value());
            log.info(source.value());
        } else {
            DynamicManagedTransaction.resetConnection();
            DynamicDataSourceHolder.removeDataSource();
        }
    }

}
