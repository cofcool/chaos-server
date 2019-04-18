package net.cofcool.chaos.server.common.core;

import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 异常描述信息
 *
 * @author CofCool
 */
public interface ExceptionCodeDescriptor {

    /**
     * 成功
     */
    String SERVER_OK = "00";
    String SERVER_OK_DESC = "serverOk";


    /**
     * 内部错误
     */
    String SERVER_ERR = String
        .valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    String SERVER_ERR_DESC = "serverError";


    /**
     * 未登录, 或登录过期
     */
    String NO_LOGIN = "A1";
    String NO_LOGIN_DESC = "noLogin";

    /**
     * 没有权限
     */
    String NO_ACCESS = "A2";
    String NO_ACCESS_DESC = "noAccess";

    /**
     * 禁止登录
     */
    String DENIAL_AUTH = "A3";
    String DENIAL_AUTH_DESC = "denialAuth";

    /**
     * 密码错误
     */
    String USER_PASSWORD_ERROR = "A4";
    String USER_PASSWORD_ERROR_DESC = "userPasswordError";

    /**
     * 验证码错误
     */
    String CAPTCHA_ERROR = "A5";
    String CAPTCHA_ERROR_DESC = "captchaError";

    /**
     * 用户不存在
     */
    String USER_NOT_EXITS = "A6";
    String USER_NOT_EXITS_DESC = "userNotExists";

    /**
     * 用户名错误
     */
    String USERNAME_ERROR = "A7";
    String USERNAME_ERROR_DESC = "usernameError";

    /**
     * 拒绝设备
     */
    String DENIAL_DEVICE = "A8";
    String DENIAL_DEVICE_DESC = "denialDevice";

    /**
     * API版本低
     */
    String LOWEST_LEVEL_API = "A9";
    String LOWEST_LEVEL_API_DESC = "lowestApiVersion";

    /**
     * 授权错误
     */
    String AUTH_ERROR = "A10";
    String AUTH_ERROR_DESC = "authorizationError";


    /**
     * 存在不能为空的参数
     */
    String PARAM_NULL = "N0";
    String PARAM_NULL_DESC = "paramNull";

    /**
     * 参数错误
     */
    String PARAM_ERROR = "N1";
    String PARAM_ERROR_DESC = "paramError";


    /**
     * 操作失败
     */
    String OPERATION_ERR = "B0";
    String OPERATION_ERR_DESC = "operatingError";

    /**
     * 拒绝操作
     */
    String DENIAL_OPERATING = "B1";
    String DENIAL_OPERATING_DESC = "denialOperating";


    /**
     * 数据已存在
     */
    String DATA_EXISTS = "D1";
    String DATA_EXISTS_DESC = "dataExists";

    /**
     * 数据错误
     */
    String DATA_ERROR = "D2";
    String DATA_ERROR_DESC = "dataError";


    /**
     * 获取异常描述码
     * @param type 异常类型
     * @return 异常描述码
     */
    String getCode(String type);

    /**
     * 获取异常描述信息
     * @param type 异常类型
     * @return 异常描述信息
     */
    String getDescription(String type);

    /**
     * 创建 ExceptionCode 实例, {@link ExceptionCode} 封装了异常描述信息, 包含"code"和"description"
     * @param type 异常类型
     * @return ExceptionCode 实例
     */
    default ExceptionCode wrap(String type) {
        return new ExceptionCode(getCode(type), getDescription(type));
    }

    @Data
    @AllArgsConstructor
    class ExceptionCode {

        String code;

        String description;

    }

}