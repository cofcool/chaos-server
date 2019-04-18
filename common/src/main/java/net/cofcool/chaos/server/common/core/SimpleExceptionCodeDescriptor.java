package net.cofcool.chaos.server.common.core;

import java.util.HashMap;
import java.util.Map;

/**
 * ExceptionCodeDescriptor 的默认实现, 处理默认信息, 包括 {@link ExceptionCodeDescriptor#SERVER_ERR} 等,
 *  应用可使用 {@link ResourceExceptionCodeDescriptor}, 该类可读取"Spring"配置的"message"信息
 *
 * @author CofCool
 */
public class SimpleExceptionCodeDescriptor implements ExceptionCodeDescriptor {

    private static final Map<String, ExceptionCode> DEFAULT_EXCEPTION_MESSAGES = new HashMap<>();

    /**
     * 获取默认的 ExceptionCodeDescriptor
     */
    protected static final ExceptionCodeDescriptor DEFAULT_DESCRIPTOR = new SimpleExceptionCodeDescriptor();

    static {
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_OK, new ExceptionCode(SERVER_OK, SERVER_OK_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(NO_LOGIN, new ExceptionCode(NO_LOGIN, NO_LOGIN_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(NO_ACCESS, new ExceptionCode(NO_ACCESS, NO_ACCESS_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_NULL, new ExceptionCode(PARAM_NULL, PARAM_NULL_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_ERROR, new ExceptionCode(PARAM_ERROR, PARAM_ERROR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(OPERATION_ERR, new ExceptionCode(OPERATION_ERR, OPERATION_ERR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_ERR, new ExceptionCode(SERVER_ERR, SERVER_ERR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_AUTH, new ExceptionCode(DENIAL_AUTH, DENIAL_AUTH_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_EXISTS, new ExceptionCode(DATA_EXISTS, DATA_EXISTS_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(USER_PASSWORD_ERROR, new ExceptionCode(USER_PASSWORD_ERROR, USER_PASSWORD_ERROR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(CAPTCHA_ERROR, new ExceptionCode(CAPTCHA_ERROR, CAPTCHA_ERROR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(USER_NOT_EXITS, new ExceptionCode(USER_NOT_EXITS, USER_NOT_EXITS_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(USERNAME_ERROR, new ExceptionCode(USERNAME_ERROR, USERNAME_ERROR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_DEVICE, new ExceptionCode(DENIAL_DEVICE, DENIAL_DEVICE_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(LOWEST_LEVEL_API, new ExceptionCode(LOWEST_LEVEL_API, LOWEST_LEVEL_API_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_OPERATING, new ExceptionCode(DENIAL_OPERATING, DENIAL_OPERATING_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_ERROR, new ExceptionCode(DATA_ERROR, DATA_ERROR_DESC));
        DEFAULT_EXCEPTION_MESSAGES.put(AUTH_ERROR, new ExceptionCode(AUTH_ERROR, AUTH_ERROR_DESC));
    }

    @Override
    public String getCode(String type) {
        return type;
    }

    @Override
    public String getDescription(String type) {
        return wrap(type).description;
    }

    @Override
    public ExceptionCode wrap(String type) {
        return DEFAULT_EXCEPTION_MESSAGES.getOrDefault(type, new ExceptionCode(type, type));
    }

}
