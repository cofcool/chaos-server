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
 * {@link Device} 抽象实现
 *
 * @author CofCool
 */
public abstract class AbstractDevice implements Device {

    private String identifier;

    private String desc;

    private AbstractDevice() {

    }

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

}
