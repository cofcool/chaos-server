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
 * Device 简单实现
 *
 * @author CofCool
 */
public class SimpleDevice extends AbstractDevice {

    private static final long serialVersionUID = -2925551374324782406L;

    public static final String IDENTIFIER_BROWSER= "0";
    public static final String IDENTIFIER_CLIENT = "1";

    /**
     * 浏览器
     */
    public static final Device BROWSER = new SimpleDevice(IDENTIFIER_BROWSER, "Browser");

    /**
     * 客户端
     */
    public static final Device CLIENT = new SimpleDevice(IDENTIFIER_CLIENT, "Client");

    private SimpleDevice(String identifier, String desc) {
        super(identifier, desc);
    }

    public String getIdentifier() {
        return identifier();
    }

    public String getDescription() {
        return desc();
    }

}
