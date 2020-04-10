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

import javax.annotation.Nonnull;

/**
 * Device 默认实现
 *
 * @author CofCool
 */
public enum DefaultDevice implements Device {

    /**
     * 火狐浏览器
     */
    FIREFOX("Firefox", "Firefox Browser"),

    /**
     * Chrome 浏览器
     */
    CHROME("Chrome", "Chrome Browser"),

    /**
     * Postman 客户端
     */
    POSTMAN("PostmanRuntime", "Postman client"),

    /**
     * 未知
     */
    UNKNOWN("unknown", "unknown");

    DefaultDevice(String identifier, String desc) {
        this.identifier = identifier;
        this.desc = desc;
    }

    private final String identifier;
    private final String desc;

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public boolean contained(@Nonnull String... identifiers) {
        boolean flag = identifiers.length == 0 || ALLOW_ALL.equals(identifiers[0]);

        int i = 0;
        while (i < identifiers.length && !flag) {
            flag = identifiers[i].contains(identifier);
            i++;
        }

        return flag;
    }


    /**
     * 根据 "User-Agent" 判断对应设备
     * @param userAgent User-Agent
     * @return "User-Agent" 对应的设备
     */
    public static Device fromUserAgent(String userAgent) {
        if (userAgent == null) {
            return DefaultDevice.UNKNOWN;
        }

        for (Device device : DefaultDevice.values()) {
            if (device.contained(userAgent)) {
                return device;
            }
        }

        return DefaultDevice.UNKNOWN;
    }

}
