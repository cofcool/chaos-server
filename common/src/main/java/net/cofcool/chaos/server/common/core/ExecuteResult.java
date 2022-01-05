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

import java.util.NoSuchElementException;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * 执行结果, 如果 {@link #successful()} 为 {@code true}, 那 {@code entity} 必须有值
 *
 * @author CofCool
 */
public interface ExecuteResult<T> extends Result<T> {

    /**
     * 获取结果数据, 建议先调用 {@link #successful()} 确保执行成功
     *
     * @throws NoSuchElementException 执行未成功或数据不存在时抛出异常
     */
    T entity();

    /**
     * 执行状态, 参考 {@link net.cofcool.chaos.server.common.core.Result.ResultState}
     */
    ResultState state();


    @Override
    default boolean successful() {
        return state() == ResultState.SUCCESSFUL;
    }

    /**
     * 获取结果数据, 如果为 {@literal null}, 则返回 {@code newVal}
     * @param newVal 替代数据
     * @return 结果数据
     */
    T orElse(T newVal);

    /**
     * 如果 <b>Entity</b> 不为 {@literal null}, 则执行 {@code fn}, 否则返回 {@literal null}
     *
     * @param fn 需要执行的 Function
     * @param <R> 返回数据类型
     * @return fn 执行结果
     */
    @Nullable
    default <R> R apply(Function<T, R> fn) {
        if (orElse(null) != null) {
            return fn.apply(entity());
        }

        return null;
    }

    /**
     * 创建 {@code ExecuteResult} 实例的方便方法, 推荐应用封装该方法，简化使用, 例如:
     *
     * <pre class="code">
     * public interface MyExecuteResult {
     *
     *     static <T> ExecuteResult<T> of(T entity, ResultState state) {
     *         if (state == ResultState.SUCCESSFUL) {
     *             return ExecuteResult.of(entity, state, ExceptionCodeDescriptor.SERVER_OK, ExceptionCodeDescriptor.SERVER_OK_DESC);
     *         } else {
     *             return ExecuteResult.of(entity, state, ExceptionCodeDescriptor.OPERATION_ERR, ExceptionCodeDescriptor.OPERATION_ERR_DESC);
     *         }
     *     }
     *
     * }
     * </pre>
     *
     * @param entity 结果数据
     * @param state 执行状态
     * @param code 描述码
     * @param msg 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     *
     * @see SimpleExecuteResult
     * @see ConfigurationSupport#getExecuteResult(Object, ResultState, String)
     */
    static <T> ExecuteResult<T> of(T entity, ResultState state, String code, String msg) {
        return new SimpleExecuteResult<>(state, Message.of(code, msg, entity));
    }

    /**
     * 创建 {@code ExecuteResult} 实例的方便方法
     *
     * @param state 执行状态
     * @param msg 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     *
     * @see SimpleExecuteResult
     */
    static <T> ExecuteResult<T> of(ResultState state, Message<T> msg) {
        return new SimpleExecuteResult<>(state, msg);
    }

}
