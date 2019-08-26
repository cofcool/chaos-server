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

import net.cofcool.chaos.server.common.core.Result.ResultState;

/**
 * 自定义 {@link Message}, {@link Result} 等接口的实现方式
 *
 * @see ConfigurationSupport
 *
 * @author CofCool
 */
public interface ConfigurationCustomizer {

    /**
     * 创建 {@link Message} 实例，应用可创建自定义 {@link Message}
     * @param code 状态码
     * @param msg 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return {@link Message} 实例
     */
    default <T> Message<T> newMessage(String code, String msg, T data) {
        return Message.of(code, msg, data);
    }

    /**
     * 创建 {@link QueryResult} 实例
     *
     * @param pageMessage 分页数据
     * @return {@link QueryResult} 实例
     */
    default <T> QueryResult<T, ?> newQueryResult(Message<Page<T>> pageMessage) {
        return QueryResult.of(pageMessage);
    }

    /**
     * 创建 {@code ExecuteResult} 实例
     *
     * @param state 执行状态
     * @param message 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     */
    default <T> ExecuteResult<T> newExecuteResult(ResultState state, Message<T> message) {
        return ExecuteResult.of(state, message);
    }

}
