/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.openfeign;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * A factory that creates instances of feign classes. It creates a Spring
 * ApplicationContext per client name, and extracts the beans that it needs from there.
 *
 * 注意: 移除了 Spring Cloud 的相关代码
 *
 * @author Spencer Gibb
 * @author Dave Syer
 */
public class FeignContext implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public <T> T getInstance(String name, Class<T> type) {
        ListableBeanFactory context = getContext(name);
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
            type).length > 0) {
            return context.getBean(type);
        }
        return null;
    }

    private ConfigurableApplicationContext getContext(String name) {
        return (ConfigurableApplicationContext) applicationContext;
    }

    public <T> Map<String, T> getInstances(String name, Class<T> type) {
        ListableBeanFactory context = getContext(name);
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
            type).length > 0) {
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(context, type);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
