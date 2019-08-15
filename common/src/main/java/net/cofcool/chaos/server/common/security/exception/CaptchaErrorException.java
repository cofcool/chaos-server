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

package net.cofcool.chaos.server.common.security.exception;

/**
 * 验证码错误时抛出
 */
public class CaptchaErrorException extends AuthorizationException {

    private static final long serialVersionUID = -2198452931188019140L;

    public CaptchaErrorException(String message, String code) {
        super(message, code);
    }

    @Override
    public int level() {
        return LOWEST_LEVEL;
    }
}
