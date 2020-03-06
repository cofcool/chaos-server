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

package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * 设备定义
 *
 * @author CofCool
 */
public interface Device extends Serializable {

    String ALLOW_ALL = "all";

    /**
     * 设备惟一标识
     */
    String identifier();

    /**
     * 描述
     */
    String desc();

    /**
     * 是否包含
     */
    default boolean contained(@Nonnull String... identifiers) {
        boolean flag = identifiers.length == 0 || ALLOW_ALL.equals(identifiers[0]);

        int i = 0;
        while (i < identifiers.length && !flag) {
            flag = identifier().equals(identifiers[i]);
            i++;
        }

        return flag;
    }

}
