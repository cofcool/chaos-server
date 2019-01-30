package net.cofcool.chaos.server.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验 http header
 *
 * @author CofCool
 */
public abstract class HttpHeaderRequestChecker extends AbstractRequestChecker {

    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) {
        return check(request.getHeader(getName()));
    }

    @Override
    public Type getType() {
        return Type.HEADER;
    }

}
