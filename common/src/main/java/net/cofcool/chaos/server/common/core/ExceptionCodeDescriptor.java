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
 * 异常描述信息, 包括异常描述码和异常描述信息, 应用可自行定义, 通过 {@link ExceptionCodeManager} 处理应用的状态码及状态描述等, 实现可参考:
 *
 * <ul>
 *     <li>net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor</li>
 *     <li>net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor</li>
 * </ul>
 *
 * @author CofCool
 *
 * @see ExceptionCodeManager
 */
public interface ExceptionCodeDescriptor {

    /**
     * 成功
     */
    String SERVER_OK = "SERVER_OK";


    /**
     * 内部错误
     */
    String SERVER_ERR = "SERVER_ERR";


    /**
     * 未登录, 或登录过期
     */
    String NO_LOGIN = "NO_LOGIN";

    /**
     * 没有权限
     */
    String NO_ACCESS = "NO_ACCESS";

    /**
     * 禁止登录
     */
    String DENIAL_AUTH = "DENIAL_AUTH";

    /**
     * 密码错误
     */
    String USER_PASSWORD_ERROR = "USER_PASSWORD_ERROR";

    /**
     * 验证码错误
     */
    String CAPTCHA_ERROR = "CAPTCHA_ERROR";

    /**
     * 用户不存在
     */
    String USER_NOT_EXITS = "USER_NOT_EXITS";

    /**
     * 用户名错误
     */
    String USERNAME_ERROR = "USERNAME_ERROR";

    /**
     * 拒绝设备
     */
    String DENIAL_DEVICE = "DENIAL_DEVICE";

    /**
     * API版本低
     */
    String LOWEST_LEVEL_API = "LOWEST_LEVEL_API";

    /**
     * 授权错误
     */
    String AUTH_ERROR = "AUTH_ERROR";


    /**
     * 存在不能为空的参数
     */
    String PARAM_NULL = "PARAM_NULL";

    /**
     * 参数错误
     */
    String PARAM_ERROR = "PARAM_ERROR";


    /**
     * 操作失败
     */
    String OPERATION_ERR = "OPERATION_ERR";

    /**
     * 拒绝操作
     */
    String DENIAL_OPERATING = "DENIAL_OPERATING";


    /**
     * 数据已存在
     */
    String DATA_EXISTS = "DATA_EXISTS";

    /**
     * 数据错误
     */
    String DATA_ERROR = "DATA_ERROR";


    /**
     * 获取异常描述码, 如不存在则直接返回 {@literal type}
     * @param type 异常类型
     * @return 异常描述码
     */
    String code(String type);

    /**
     * 获取异常描述信息, 如不存在则直接返回 {@literal type}
     * @param type 异常类型
     * @return 异常描述信息
     */
    String description(String type);

    /**
     * 异常信息
     */
    final class CodeMessage {

        private final String code;
        private final String desc;

        private CodeMessage(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        /**
         * 异常描述码
         * @return 异常描述码
         */
        public String code() {
            return code;
        }

        /**
         * 异常描述信息
         * @return 异常描述信息
         */
        public String desc() {
            return desc;
        }

    }

    /**
     * 创建异常信息实例
     * @param code 异常描述码
     * @param desc 异常描述信息
     * @return 异常信息实例
     */
    static CodeMessage message(String code, String desc) {
        return new CodeMessage(code, desc);
    }

}