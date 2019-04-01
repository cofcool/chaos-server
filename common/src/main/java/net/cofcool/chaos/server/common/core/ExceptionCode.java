package net.cofcool.chaos.server.common.core;

import javax.servlet.http.HttpServletResponse;

/**
 * 定义异常码
 *
 * @author CofCool
 */
public final class ExceptionCode {

    /**
     * 成功
     */
    public static final String SERVER_OK = "00";

    /**
     * 未登录, 或登录过期
     */
    public static final String NO_LOGIN = "A1";

    /**
     * 没有权限
     */
    public static final String NO_ACCESS = "A2";

    /**
     * 存在不能为空的参数
     */
    public static final String PARAM_NULL = "N0";

    /**
     * 参数错误
     */
    public static final String PARAM_ERROR = "N1";

    /**
     * 操作失败
     */
    public static final String OPERATION_ERR = "B0";

    /**
     * 内部错误
     */
    public static final String SERVER_ERR = String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    /**
     * 禁止登录
     */
    public static final String DENIAL_AUTH = "A3";

    /**
     * 数据已存在
     */
    public static final String DATA_EXISTS = "D1";

    /**
     * 成功
     */
    public static String SERVER_OK_KEY = "serverOk";

    /**
     * 未登录, 或登录过期
     */
    public static String NO_LOGIN_KEY = "noLogin";

    /**
     * 没有权限
     */
    public static String NO_ACCESS_EKY = "noAccess";

    /**
     * 存在不能为空的参数
     */
    public static String PARAM_NULL_KEY = "paramNull";

    /**
     * 参数错误
     */
    public static String PARAM_ERROR_KEY = "paramError";

    /**
     * 操作失败
     */
    public static String OPERATION_ERR_KEY = "operatingError";

    /**
     * 内部错误
     */
    public static String SERVER_ERR_KEY = "serverError";

    /**
     * 拒绝授权
     */
    public static String DENIAL_AUTH_KEY = "denialAuth";

    /**
     * 数据已存在
     */
    public static String DATA_EXISTS_KEY = "dataExists";

    /**
     * 数据错误
     */
    public static String DATA_ERROR_KEY = "dataError";

    /**
     * 密码错误
     */
    public static String USER_PASSWORD_ERROR_KEY = "userPasswordError";

    /**
     * 验证码错误
     */
    public static String CAPTCHA_ERROR_KEY = "captchaError";

    /**
     * 用户不存在
     */
    public static String USER_NOT_EXITS_KEY = "userNotExists";

    /**
     * 用户名错误
     */
    public static String USERNAME_ERROR_KEY = "usernameError";

    /**
     * 拒绝设备
     */
    public static String DENIAL_DEVICE_KEY = "denialDevice";

    /**
     * 拒绝操作
     */
    public static String DEINAL_OPERATING_KEY = "denialOperating";

    /**
     * API版本低
     */
    public static String LOW_LEVEL_API_KEY = "lowApiVersion";

}