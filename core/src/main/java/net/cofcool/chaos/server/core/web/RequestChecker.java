package net.cofcool.chaos.server.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求合法性校验
 *
 * @author CofCool
 */
public interface RequestChecker {

    /**
     * 进行校验
     *
     * @param request 请求
     * @param response 响应
     * @return 校验结果
     *
     * @throws net.cofcool.chaos.server.common.core.ServiceException 如果想返回校验失败原因, 可通过此异常进行处理
     */
    boolean check(HttpServletRequest request, HttpServletResponse response);

    /**
     * 参数携带方式
     *
     * @return {@link Type}
     */
    Type getType();


    /**
     * 参数携带方式
     */
    enum Type {
        HEADER, QUERY_STRING, BODY
    }

}
