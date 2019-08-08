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

/**
 * 异常等级
 *
 * @author CofCool
 */
public interface ExceptionLevel {

    /**
     * 最高级别
     */
    int HIGHEST_LEVEL = Integer.MIN_VALUE;

    /**
     * 最低级别
     */
    int LOWEST_LEVEL = Integer.MAX_VALUE;

    /**
     * 普通等级, 高于该等级的异常会输出日志
     */
    int NORMAL_LEVEL = 0;

    int level();

    /**
     * 是否输出日志, true则表示输出日志
     * @return 是否输出日志
     */
    default boolean showable() {
        return level() < NORMAL_LEVEL;
    }

}
