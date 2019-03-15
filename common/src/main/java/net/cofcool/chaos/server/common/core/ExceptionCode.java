package net.cofcool.chaos.server.common.core;

import javax.servlet.http.HttpServletResponse;

/**
 * 定义异常码
 *
 * @author CofCool
 */
public final class ExceptionCode {

    /**
     *成功
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
    public static final String SERVER_ERR = String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    /**
     * 禁止登录
     */
    public static final String DENIAL_AUTH = "A3";

    /**
     * 数据已存在
     */
    public static final String DATA_EXISTS = "D1";

}
