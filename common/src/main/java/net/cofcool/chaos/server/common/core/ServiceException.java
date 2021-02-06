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
 * 自定义异常, Service层异常
 *
 * @author CofCool
 */
public class ServiceException extends RuntimeException implements ExceptionLevel {

    private static final long serialVersionUID = 3055975161234192313L;

    private final String code;

    private final int level;

    public ServiceException(String message) {
        this(message, null, HIGHEST_LEVEL);
    }

    public ServiceException(String message, String code) {
        this(message, code, HIGHEST_LEVEL);
    }

    public ServiceException(String message, String code, int level) {
        this(message, code, level, null);
    }

    public ServiceException(String message, Throwable cause) {
        this(message, null, ExceptionLevel.NORMAL_LEVEL, cause);
    }

    public ServiceException(String message, String code, int level, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int level() {
        return level;
    }
}
