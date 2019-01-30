package net.cofcool.chaos.server.core.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验 Url
 *
 * @author CofCool
 */
public abstract class UrlRequestChecker extends AbstractRequestChecker {

    @Override
    public boolean check(HttpServletRequest request, HttpServletResponse response) {
        return check(request.getParameter(getName()));
    }

    @Override
    public Type getType() {
        return Type.QUERY_STRING;
    }

}
