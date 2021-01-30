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

package net.cofcool.chaos.server.common.core;


import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 基础 {@code Service}, 简化查询操作
 *
 * @see DataAccess
 *
 * @author CofCool
 */
public abstract class SimpleService<T> implements DataAccess<T> {

    private PageProcessor<T> pageProcessor;

    protected PageProcessor<T> getPageProcessor() {
        return pageProcessor;
    }

    public void setPageProcessor(PageProcessor<T> pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    private ConfigurationSupport configuration;

    protected ConfigurationSupport getConfiguration() {
        return configuration;
    }

    @Autowired
    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
    }

    @Override
    public QueryResult<T, ?> query(Page<T> condition, T entity) {
        Page<T> page = Page.checkPage(condition);

        Objects.requireNonNull(getPageProcessor());
        return configuration.getQueryResult(
            getPageProcessor().process(page, queryWithPage(page, entity)),
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    /**
     * 分页查询
     *
     * @param condition 条件
     * @param entity 实体
     *
     * @return 查询结果, 需和 {@link #pageProcessor} 支持类型一致
     */
    protected abstract Object queryWithPage(Page<T> condition, T entity);

}
