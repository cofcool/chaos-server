package net.cofcool.chaos.server.core.support;

import java.util.HashMap;
import java.util.Map;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.core.i18n.ResourceExceptionCodeDescriptor;

/**
 * ExceptionCodeDescriptor 的默认实现, 处理默认信息, 包括 {@link ExceptionCodeDescriptor#SERVER_ERR} 等,
 *  应用可使用 {@link ResourceExceptionCodeDescriptor}, 该类可读取"Spring"配置的"message"信息
 * @author CofCool
 */
public class SimpleExceptionCodeDescriptor implements ExceptionCodeDescriptor {

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

    private static final Map<String, String> DEFAULT_EXCEPTION_MESSAGES = new HashMap<>();

    static {
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_OK, SERVER_OK_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_OK_DESC, SERVER_OK_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_ERR, SERVER_ERR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(SERVER_ERR_DESC, SERVER_ERR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(NO_LOGIN, NO_LOGIN_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(NO_LOGIN_DESC, NO_LOGIN_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(NO_ACCESS, NO_ACCESS_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(NO_ACCESS_DESC, NO_ACCESS_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_AUTH, DENIAL_AUTH_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_AUTH_DESC, DENIAL_AUTH_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(USER_PASSWORD_ERROR, USER_PASSWORD_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(USER_PASSWORD_ERROR_DESC, USER_PASSWORD_ERROR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(CAPTCHA_ERROR, CAPTCHA_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(CAPTCHA_ERROR_DESC, CAPTCHA_ERROR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(USER_NOT_EXITS, USER_NOT_EXITS_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(USER_NOT_EXITS_DESC, USER_NOT_EXITS_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(USERNAME_ERROR, USERNAME_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(USERNAME_ERROR_DESC, USERNAME_ERROR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_DEVICE, DENIAL_DEVICE_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_DEVICE_DESC, DENIAL_DEVICE_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(LOWEST_LEVEL_API, LOWEST_LEVEL_API_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(LOWEST_LEVEL_API_DESC, LOWEST_LEVEL_API_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(AUTH_ERROR, AUTH_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(AUTH_ERROR_DESC, AUTH_ERROR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_NULL, PARAM_NULL_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_NULL_DESC, PARAM_NULL_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_ERROR, PARAM_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(PARAM_ERROR_DESC, PARAM_ERROR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(OPERATION_ERR, OPERATION_ERR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(OPERATION_ERR_DESC, OPERATION_ERR_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_OPERATING, DENIAL_OPERATING_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(DENIAL_OPERATING_DESC, DENIAL_OPERATING_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(DATA_EXISTS, DATA_EXISTS_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_EXISTS_DESC, DATA_EXISTS_DESC_VAL);

        DEFAULT_EXCEPTION_MESSAGES.put(DATA_ERROR, DATA_ERROR_VAL);
        DEFAULT_EXCEPTION_MESSAGES.put(DATA_ERROR_DESC, DATA_ERROR_DESC_VAL);
    }

    @Override
    public String code(String type) {
        return DEFAULT_EXCEPTION_MESSAGES.getOrDefault(type, type);
    }

    @Override
    public String description(String type) {
        return DEFAULT_EXCEPTION_MESSAGES.getOrDefault(type, type);
    }

}
