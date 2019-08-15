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

package net.cofcool.chaos.server.demo.config;

import java.util.Map;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import net.cofcool.chaos.server.demo.api.Constant;

public class MyExceptionCodeDescriptor extends SimpleExceptionCodeDescriptor {

    @Override
    protected Map<String, String> customize() {
        return Map.of(
            ExceptionCodeDescriptor.USER_PASSWORD_ERROR, Constant.PASSWORD_ERROR_VAL,
            ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC, Constant.PASSWORD_ERROR_DESC_VAL
        );
    }
}
