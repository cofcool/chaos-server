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
    String SERVER_OK_DESC = "SERVER_OK_DESC";


    /**
     * 内部错误
     */
    String SERVER_ERR = "SERVER_ERR";
    String SERVER_ERR_DESC = "SERVER_ERR_DESC";


    /**
     * 未登录, 或登录过期
     */
    String NO_LOGIN = "NO_LOGIN";
    String NO_LOGIN_DESC = "NO_LOGIN_DESC";

    /**
     * 没有权限
     */
    String NO_ACCESS = "NO_ACCESS";
    String NO_ACCESS_DESC = "NO_ACCESS_DESC";

    /**
     * 禁止登录
     */
    String DENIAL_AUTH = "DENIAL_AUTH";
    String DENIAL_AUTH_DESC = "DENIAL_AUTH_DESC";

    /**
     * 密码错误
     */
    String USER_PASSWORD_ERROR = "USER_PASSWORD_ERROR";
    String USER_PASSWORD_ERROR_DESC = "USER_PASSWORD_ERROR_DESC";

    /**
     * 验证码错误
     */
    String CAPTCHA_ERROR = "CAPTCHA_ERROR";
    String CAPTCHA_ERROR_DESC = "CAPTCHA_ERROR_DESC";

    /**
     * 用户不存在
     */
    String USER_NOT_EXITS = "USER_NOT_EXITS";
    String USER_NOT_EXITS_DESC = "USER_NOT_EXITS_DESC";

    /**
     * 用户名错误
     */
    String USERNAME_ERROR = "USERNAME_ERROR";
    String USERNAME_ERROR_DESC = "USERNAME_ERROR_DESC";

    /**
     * 拒绝设备
     */
    String DENIAL_DEVICE = "DENIAL_DEVICE";
    String DENIAL_DEVICE_DESC = "DENIAL_DEVICE_DESC";

    /**
     * API版本低
     */
    String LOWEST_LEVEL_API = "LOWEST_LEVEL_API";
    String LOWEST_LEVEL_API_DESC = "LOWEST_LEVEL_API_DESC";

    /**
     * 授权错误
     */
    String AUTH_ERROR = "AUTH_ERROR";
    String AUTH_ERROR_DESC = "AUTH_ERROR_DESC";


    /**
     * 存在不能为空的参数
     */
    String PARAM_NULL = "PARAM_NULL";
    String PARAM_NULL_DESC = "PARAM_NULL_DESC";

    /**
     * 参数错误
     */
    String PARAM_ERROR = "PARAM_ERROR";
    String PARAM_ERROR_DESC = "PARAM_ERROR_DESC";


    /**
     * 操作失败
     */
    String OPERATION_ERR = "OPERATION_ERR";
    String OPERATION_ERR_DESC = "OPERATION_ERR_DESC";

    /**
     * 拒绝操作
     */
    String DENIAL_OPERATING = "DENIAL_OPERATING";
    String DENIAL_OPERATING_DESC = "DENIAL_OPERATING_DESC";


    /**
     * 数据已存在
     */
    String DATA_EXISTS = "DATA_EXISTS";
    String DATA_EXISTS_DESC = "DATA_EXISTS_DESC";

    /**
     * 数据错误
     */
    String DATA_ERROR = "DATA_ERROR";
    String DATA_ERROR_DESC = "DATA_ERROR_DESC";


    /**
     * 获取异常描述码
     * @param type 异常类型
     * @return 异常描述码
     */
    String code(String type);

    /**
     * 获取异常描述信息
     * @param type 异常类型
     * @return 异常描述信息
     */
    String description(String type);

}