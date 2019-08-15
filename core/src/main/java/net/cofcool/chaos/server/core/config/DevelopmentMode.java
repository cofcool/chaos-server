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

package net.cofcool.chaos.server.core.config;

/**
 * 项目开发模式
 *
 * @author CofCool
 */
public enum DevelopmentMode {
    /**
     * 开发模式
     */
    DEV,
    /**
     * 测试模式
     */
    TEST,
    /**
     * 发布模式
     */
    RELEASE;

    /**
     * 是否是调试模式, 在 {@link #DEV} 和 {@link #TEST} 模式下会处于调试模式
     * @return 是否是调试模式
     */
    public boolean isDebugMode() {
        return this.equals(DevelopmentMode.DEV) ||
                this.equals(DevelopmentMode.TEST);
    }


}
