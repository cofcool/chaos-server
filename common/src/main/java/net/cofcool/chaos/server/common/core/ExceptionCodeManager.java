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

import java.util.Objects;

/**
 * 异常描述管理, 建议应用维护一个实例
 *
 * @author CofCool
 *
 * @see ExceptionCodeDescriptor
 */
public class ExceptionCodeManager {

    private final ExceptionCodeDescriptor descriptor;

    /**
     * 创建异常描述管理器
     * @param descriptor ExceptionCodeDescriptor 实例，不能为 {@literal null}
     */
    public ExceptionCodeManager(ExceptionCodeDescriptor descriptor) {
        Objects.requireNonNull(descriptor);
        this.descriptor = descriptor;
    }

    /**
     * 获取 ExceptionCodeDescriptor
     * @return ExceptionCodeDescriptor 实例
     */
    public ExceptionCodeDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * 获取异常描述码, 如不存在则直接返回 type
     * @param type 异常类型
     * @return 异常描述码
     */
    public String getCode(String type) {
        return descriptor.code(type);
    }

    /**
     * 获取异常描述信息, 如不存在则直接返回 type
     * @param type 异常类型
     * @return 异常描述信息
     */
    public String getDescription(String type) {
        return descriptor.description(type);
    }

}