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

package net.cofcool.chaos.server.common.util;

import javax.annotation.Nullable;

/**
 * String 工具类
 *
 * @author CofCool
 */
public final class StringUtils {

    public static final String EMPTY_STRING = "";


    /**
     * 判断字符串是否为空
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 去除头尾空格, 如果去除后为空则返回null
     */
    public static String clean(String in) {
        String out = in;

        if (in != null) {
            out = in.trim();
            if (out.equals(EMPTY_STRING)) {
                out = null;
            }
        }

        return out;
    }

    /**
     * 获取字符长度
     */
    public static int length(@Nullable CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

}
