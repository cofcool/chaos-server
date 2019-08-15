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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验 http header
 *
 * @author CofCool
 */
public abstract class HttpHeaderRequestChecker extends AbstractRequestChecker {

    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) {
        return check(request.getHeader(getName()));
    }

    @Override
    public Type getType() {
        return Type.HEADER;
    }

}
