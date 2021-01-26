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

package net.cofcool.chaos.server.core.web;


import javax.annotation.Nullable;

/**
 * 校验请求的抽象实现
 *
 * @author CofCool
 */
public abstract class AbstractRequestChecker implements RequestChecker {

    /**
     * 参数名
     *
     * @return 参数名
     */
    protected abstract String getName();

    /**
     * 校验
     * @param value 待检查值
     * @return 检查是否正常
     */
    protected abstract boolean check(@Nullable String value);

}
