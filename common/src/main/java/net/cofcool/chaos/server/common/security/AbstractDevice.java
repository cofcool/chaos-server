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

/**
 * {@link Device} 子类可继承该类, 简化代码,
 * 如果不想使用此方式, 也可参考 {@link DefaultDevice}
 *
 * @author CofCool
 */
public abstract class AbstractDevice implements Device {

    private final String identifier;

    private final String desc;

    /**
     * 创建 {@code Device}, 建议子类声明为 {@literal protect} 或 {@literal private}, 该类为不可变对象
     * @param identifier 设备惟一标识
     * @param desc 描述
     */
    protected AbstractDevice(String identifier, String desc) {
        this.identifier = identifier;
        this.desc = desc;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String toString() {
        return "AbstractDevice{" +
            "identifier='" + identifier + '\'' +
            ", desc='" + desc + '\'' +
            '}';
    }

    // just for json serializer
    public String getDesc() {
        return desc;
    }

    public String getIdentifier() {
        return identifier;
    }
}
