package net.cofcool.chaos.server.demo.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import net.cofcool.chaos.server.common.security.AbstractLogin;


public class Login extends AbstractLogin {

    private static final long serialVersionUID = -3868201780589666741L;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    protected net.cofcool.chaos.server.common.security.Device checkRequestAgent(HttpServletRequest servletRequest) {
       return Device.BROWSER;
    }
}
