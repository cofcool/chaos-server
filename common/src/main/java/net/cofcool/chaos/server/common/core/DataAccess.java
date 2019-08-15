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

import java.util.List;
import net.cofcool.chaos.server.common.core.Result.ResultState;

/**
 * 定义常用的数据查询删除等相关方法
 *
 * @author CofCool
 */
public interface DataAccess<T> {

    /**
     * 添加
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> add(T entity);

    /**
     * 删除
     *
     * @param entity 实体
     *
     * @return 执行状态
     */
    ResultState delete(T entity);

    /**
     * 修改
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> update(T entity);

    /**
     * 分页查询
     *
     * @param condition 条件
     * @param entity 实体
     *
     * @return {@link QueryResult} 对象
     */
    QueryResult <T, ?> query(Page<T> condition, T entity);

    /**
     * 查询所有
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<List<T>> queryAll(T entity);

    /**
     * 查询详情
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> queryById(T entity);

}

