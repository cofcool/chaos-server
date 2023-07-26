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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 封装数据, 包含状态与描述
 *
 * @author CofCool
 */
public interface Message<T> extends Serializable {

    /**
     * 状态码
     */
    String code();

    /**
     * 描述信息
     */
    String message();

    /**
     * 携带数据
     */
    T data();

    /**
     * 标记数据是否需要被 {@link Message} 包裹, 状态和描述参考 {@link ExceptionCodeDescriptor#SERVER_OK}, 具体实现参考 {@code net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter}, 使用示例:
     * <pre>
     *     &#064;MessageWrapped
     *     class Example {
     *
     *     }
     * </pre>
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    @interface MessageWrapped {

    }


    /**
     * 创建 Message 实例的方便方法
     * @param code 状态码
     * @param msg 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return Message 实例
     */
    static <T> Message<T> of(String code, String msg, T data) {
        return new SimpleMessage<>(code, msg, data);
    }

    /**
     * 创建 Message 实例的方便方法
     * @param code 状态码
     * @param msg 描述信息
     * @param <T> 携带数据类型
     * @return Message 实例
     */
    static<T> Message<T> of(String code, String msg) {
        return new SimpleMessage<>(code, msg, null);
    }

}
