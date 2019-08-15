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

/**
 * 查询结果
 *
 * @param <T> 列表数据类型
 * @param <R> 结果数据类型
 *
 * @author CofCool
 */
public interface QueryResult<T, R> extends Result<R> {

    /**
     * 分页数据
     * @return {@link Page}
     */
    Page<T> page();

    /**
     * QueryResult 携带的额外数据
     *
     * @return 扩展数据
     */
    Object ext();

    /**
     * 创建 QueryResult 实例的方便方法
     *
     * @param page 分页数据
     * @param <T> 数据类型
     * @return QueryResult 实例
     */
    static <T> QueryResult<T, ?> of(Page<T> page, String code, String msg) {
        return new SimpleQueryResult<>(page, code, msg);
    }

    /**
     * 创建 QueryResult 实例的方便方法
     *
     * @param page 分页数据
     * @param ext 扩展数据
     * @param <T>  数据类型
     * @return QueryResult 实例
     */
    static <T> QueryResult<T, ?> of(Page<T> page, Object ext, String code, String msg) {
        return new SimpleQueryResult<>(page, ext, code, msg);
    }

}
