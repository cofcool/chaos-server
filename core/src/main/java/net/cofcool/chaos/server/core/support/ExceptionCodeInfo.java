package net.cofcool.chaos.server.core.support;

import net.cofcool.chaos.server.common.core.ExceptionCode;

/**
 * 异常描述信息，从"国际化资源文件"读取描述信息，默认使用前缀{@link #EXCEPTION_CODE_INFO_PREFIX}。
 *
 * @see ExceptionCode
 *
 * @author CofCool
 */
public class ExceptionCodeInfo {

    /**
     * 描述信息key的前缀
     */
    public static final String EXCEPTION_CODE_INFO_PREFIX = "chaos-exception.";

    protected static final ExceptionCodeManager EXCEPTION_CODE_MANAGER = ExceptionCodeManager.getInstance().setPrefix(EXCEPTION_CODE_INFO_PREFIX);


    /**
     * 成功
     */
    public static String serverOk() {
        return EXCEPTION_CODE_MANAGER.get("serverOk", true);
    }

    /**
     * 未登录，或登录过期
     */
    public static String noLogin() {
        return EXCEPTION_CODE_MANAGER.get("noLogin", true);
    }

    /**
     * 没有权限
     */
    public static String noAccess() {
        return EXCEPTION_CODE_MANAGER.get("noAccess", true);
    }

    /**
     * 存在不能为空的参数
     */
    public static String paramNull() {
        return EXCEPTION_CODE_MANAGER.get("paramNull", true);
    }

    /**
     * 参数错误
     */
    public static String paramError() {
        return EXCEPTION_CODE_MANAGER.get("paramError", true);
    }

    /**
     * 操作失败
     */
    public static String operatingError() {
        return EXCEPTION_CODE_MANAGER.get("operatingError", true);
    }

    /**
     * 内部错误
     */
    public static String serverError() {
        return EXCEPTION_CODE_MANAGER.get("serverError", true);
    }

    /**
     * 拒绝授权
     */
    public static String denialAuth() {
        return EXCEPTION_CODE_MANAGER.get("denialAuth", true);
    }

    /**
     * 数据已存在
     */
    public static String dataExists() {
        return EXCEPTION_CODE_MANAGER.get("dataExists", true);
    }

    /**
     * 数据错误
     */
    public static String dataError() {
        return EXCEPTION_CODE_MANAGER.get("dataError", true);
    }

    /**
     * 密码错误
     */
    public static String userPasswordError() {
        return EXCEPTION_CODE_MANAGER.get("userPasswordError", true);
    }

    /**
     * 验证码错误
     */
    public static String captchaError() {
        return EXCEPTION_CODE_MANAGER.get("captchaError", true);
    }

    /**
     * 用户不存在
     */
    public static String userNotExists() {
        return EXCEPTION_CODE_MANAGER.get("userNotExists", true);
    }

    /**
     * 用户名错误
     */
    public static String usernameError() {
        return EXCEPTION_CODE_MANAGER.get("usernameError", true);
    }

    /**
     * 拒绝设备
     */
    public static String denialDevice() {
        return EXCEPTION_CODE_MANAGER.get("denialDevice", true);
    }

    /**
     * 拒绝操作
     */
    public static String denialOperating() {
        return EXCEPTION_CODE_MANAGER.get("denialOperating", true);
    }

    /**
     * API版本低
     */
    public static String lowApiVersion() {
        return EXCEPTION_CODE_MANAGER.get("lowApiVersion", true);
    }

}
