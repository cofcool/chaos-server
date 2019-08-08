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

import java.io.Serializable;

/**
 * 封装运行结果
 *
 * @author CofCool
 */
public interface Result<T> extends Serializable {

    /**
     * 执行状态
     */
    enum ResultState {
        /**
         * 成功
         */
        SUCCESSFUL,
        /**
         * 失败
         */
        FAILURE,
        /**
         * 等待中
         */
        WAITING,
        /**
         * 未知
         */
        UNKNOWN
    }


    /**
     * 结果数据
     *
     * @return 封装的Message对象
     *
     * @see Message
     */
    Message<T> result();

    /**
     * 执行是否成功
     * @return 执行是否成功
     */
    default boolean successful() {
        return true;
    }

}
