package net.cofcool.chaos.server.demo.config;

import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.authorization.AuthService;

/**
 * @author CofCool
s*/
public class AuthServiceImpl implements AuthService {


    @Override
    public Message<User> login(AbstractLogin loginUser) {
        return null;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public boolean checkPermission(ServletRequest servletRequest, ServletResponse servletResponse) {
        return false;
    }

    @Nullable
    @Override
    public User readCurrentUser() {
        return null;
    }

}
