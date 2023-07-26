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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 请求合法性校验
 *
 * @author CofCool
 */
public interface RequestChecker {

    /**
     * 进行校验
     *
     * @param request 请求
     * @param response 响应
     * @return 校验结果
     *
     * @throws net.cofcool.chaos.server.common.core.ServiceException 如果想返回校验失败原因, 可通过此异常进行处理
     */
    boolean check(HttpServletRequest request, HttpServletResponse response);

    /**
     * 参数携带方式
     *
     * @return {@link Type}
     */
    Type getType();


    /**
     * 参数携带方式
     */
    enum Type {
        HEADER, QUERY_STRING, BODY
    }

}
