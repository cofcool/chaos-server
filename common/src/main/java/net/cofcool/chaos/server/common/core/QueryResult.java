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
     * 创建 {@link QueryResult} 实例的方便方法
     *
     * @param page 分页数据
     * @param <T> 数据类型
     * @param code 描述码
     * @param msg 描述信息
     * @return {@link QueryResult} 实例
     */
    static <T> QueryResult<T, ?> of(Page<T> page, String code, String msg) {
        return new SimpleQueryResult<>(Message.of(code, msg, page));
    }

    /**
     * 创建 {@link QueryResult} 实例的方便方法
     * @param message 分页数据
     * @param <T> 数据类型
     * @return {@link QueryResult} 实例
     */
    static <T> QueryResult<T, ?> of(Message<Page<T>> message) {
        return new SimpleQueryResult<>(message);
    }

}
