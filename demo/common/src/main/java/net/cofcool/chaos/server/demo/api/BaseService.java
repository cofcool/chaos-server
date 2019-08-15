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

package net.cofcool.chaos.server.demo.api;

import java.util.List;
import net.cofcool.chaos.server.common.core.DataAccess;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.QueryResult;

/**
 * 基础 Service, 应用内部直接继承即可
 * @param <T> 实体类型
 */
public interface BaseService<T> extends DataAccess<T> {

    /**
     * 分页查询, 简单查询, 只包含 <code>T</code> 类数据, 推荐重写该方法
     *
     * @param condition 条件
     * @param entity 实体
     *
     * @return {@link QueryResult} 对象
     */
    default QueryResult<T, ?> simpleQuery(Page<T> condition, T entity) {
        return query(condition, entity);
    }

    /**
     * 查询所有, 简单查询, 只包含 <code>T</code> 类数据, 推荐重写该方法
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    default ExecuteResult<List<T>> simpleQueryAll(T entity) {
        return queryAll(entity);
    }

    /**
     * 查询详情, 简单查询, 只包含 <code>T</code> 类数据, 推荐重写该方法
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    default ExecuteResult<T> simpleQueryById(T entity) {
        return queryById(entity);
    }
}
