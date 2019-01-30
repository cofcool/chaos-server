package net.cofcool.chaos.server.core.web;


import javax.annotation.Nullable;

/**
 * 校验请求的抽象实现
 *
 * @author CofCool
 */
public abstract class AbstractRequestChecker implements RequestChecker {

    /**
     * 参数名
     */
    protected abstract String getName();

    /**
     * 校验
     */
    protected abstract boolean check(@Nullable String value);

}
