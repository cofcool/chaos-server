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

package net.cofcool.chaos.server.core.support;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * ExceptionCodeDescriptor 的默认实现, 处理默认信息, 包括 {@link ExceptionCodeDescriptor#SERVER_ERR} 等,
 *  应用可使用 {@link net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor}, 该类可读取"Spring"配置的"message"信息
 * @see net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor
 * @author CofCool
 */
public class SimpleExceptionCodeDescriptor implements ExceptionCodeDescriptor, InitializingBean {

    /**
     * 成功
     */
    public static final String SERVER_OK_VAL = "00";
    public static final String SERVER_OK_DESC_VAL = "serverOk";


    /**
     * 内部错误
     */
    public static final String SERVER_ERR_VAL = "500";
    public static final String SERVER_ERR_DESC_VAL = "serverError";


    /**
     * 未登录, 或登录过期
     */
    public static final String NO_LOGIN_VAL = "A1";
    public static final String NO_LOGIN_DESC_VAL = "noLogin";

    /**
     * 没有权限
     */
    public static final String NO_ACCESS_VAL = "A2";
    public static final String NO_ACCESS_DESC_VAL = "noAccess";

    /**
     * 禁止登录
     */
    public static final String DENIAL_AUTH_VAL = "A3";
    public static final String DENIAL_AUTH_DESC_VAL = "denialAuth";

    /**
     * 密码错误
     */
    public static final String USER_PASSWORD_ERROR_VAL = "A4";
    public static final String USER_PASSWORD_ERROR_DESC_VAL = "userPasswordError";

    /**
     * 验证码错误
     */
    public static final String CAPTCHA_ERROR_VAL = "A5";
    public static final String CAPTCHA_ERROR_DESC_VAL = "captchaError";

    /**
     * 用户不存在
     */
    public static final String USER_NOT_EXITS_VAL = "A6";
    public static final String USER_NOT_EXITS_DESC_VAL = "userNotExists";

    /**
     * 用户名错误
     */
    public static final String USERNAME_ERROR_VAL = "A7";
    public static final String USERNAME_ERROR_DESC_VAL = "usernameError";

    /**
     * 拒绝设备
     */
    public static final String DENIAL_DEVICE_VAL = "A8";
    public static final String DENIAL_DEVICE_DESC_VAL = "denialDevice";

    /**
     * API版本低
     */
    public static final String LOWEST_LEVEL_API_VAL = "A9";
    public static final String LOWEST_LEVEL_API_DESC_VAL = "lowestApiVersion";

    /**
     * 授权错误
     */
    public static final String AUTH_ERROR_VAL = "A10";
    public static final String AUTH_ERROR_DESC_VAL = "authorizationError";


    /**
     * 存在不能为空的参数
     */
    public static final String PARAM_NULL_VAL = "N0";
    public static final String PARAM_NULL_DESC_VAL = "paramNull";

    /**
     * 参数错误
     */
    public static final String PARAM_ERROR_VAL = "N1";
    public static final String PARAM_ERROR_DESC_VAL = "paramError";


    /**
     * 操作失败
     */
    public static final String OPERATION_ERR_VAL = "B0";
    public static final String OPERATION_ERR_DESC_VAL = "operatingError";

    /**
     * 拒绝操作
     */
    public static final String DENIAL_OPERATING_VAL = "B1";
    public static final String DENIAL_OPERATING_DESC_VAL = "denialOperating";


    /**
     * 数据已存在
     */
    public static final String DATA_EXISTS_VAL = "D1";
    public static final String DATA_EXISTS_DESC_VAL = "dataExists";

    /**
     * 数据错误
     */
    public static final String DATA_ERROR_VAL = "D2";
    public static final String DATA_ERROR_DESC_VAL = "dataError";

    /**
     * 获取默认的 ExceptionCodeDescriptor
     */
    public static final ExceptionCodeDescriptor DEFAULT_DESCRIPTOR = new SimpleExceptionCodeDescriptor();

    private static final Map<String, CodeMessage> DEFAULT_EXCEPTION_MESSAGES = new HashMap<>();

    static {
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_OK, ExceptionCodeDescriptor.message(SERVER_OK_VAL, SERVER_OK_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_ERR, ExceptionCodeDescriptor.message(SERVER_ERR_VAL, SERVER_ERR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(NO_LOGIN, ExceptionCodeDescriptor.message(NO_LOGIN_VAL, NO_LOGIN_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(NO_ACCESS, ExceptionCodeDescriptor.message(NO_ACCESS_VAL, NO_ACCESS_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_AUTH, ExceptionCodeDescriptor.message(DENIAL_AUTH_VAL, DENIAL_AUTH_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(USER_PASSWORD_ERROR, ExceptionCodeDescriptor.message(USER_PASSWORD_ERROR_VAL, USER_PASSWORD_ERROR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(CAPTCHA_ERROR, ExceptionCodeDescriptor.message(CAPTCHA_ERROR_VAL, CAPTCHA_ERROR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(USER_NOT_EXITS, ExceptionCodeDescriptor.message(USER_NOT_EXITS_VAL, USER_NOT_EXITS_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(USERNAME_ERROR, ExceptionCodeDescriptor.message(USERNAME_ERROR_VAL, USERNAME_ERROR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_DEVICE, ExceptionCodeDescriptor.message(DENIAL_DEVICE_VAL, DENIAL_DEVICE_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(LOWEST_LEVEL_API, ExceptionCodeDescriptor.message(LOWEST_LEVEL_API_VAL, LOWEST_LEVEL_API_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(AUTH_ERROR, ExceptionCodeDescriptor.message(AUTH_ERROR_VAL, AUTH_ERROR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_NULL, ExceptionCodeDescriptor.message(PARAM_NULL_VAL, PARAM_NULL_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_ERROR, ExceptionCodeDescriptor.message(PARAM_ERROR_VAL, PARAM_ERROR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(OPERATION_ERR, ExceptionCodeDescriptor.message(OPERATION_ERR_VAL, OPERATION_ERR_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_OPERATING, ExceptionCodeDescriptor.message(DENIAL_OPERATING_VAL, DENIAL_OPERATING_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_EXISTS, ExceptionCodeDescriptor.message(DATA_EXISTS_VAL, DATA_EXISTS_DESC_VAL));
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_ERROR, ExceptionCodeDescriptor.message(DATA_ERROR_VAL, DATA_ERROR_DESC_VAL));
    }

    @Override
    public String code(String type) {
        CodeMessage message = DEFAULT_EXCEPTION_MESSAGES.get(type);
        if (message == null) {
            return type;
        }
        return message.code();
    }

    @Override
    public String description(String type) {
        CodeMessage message = DEFAULT_EXCEPTION_MESSAGES.get(type);
        if (message == null) {
            return type;
        }
        return message.desc();
    }

    /**
     * 添加自定义描述信息
     * @return 描述信息
     */
    protected Map<String, CodeMessage> customize() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, CodeMessage> customizeMap = customize();
        if (customizeMap != null) {
            DEFAULT_EXCEPTION_MESSAGES.putAll(customizeMap);
        }
    }
}
