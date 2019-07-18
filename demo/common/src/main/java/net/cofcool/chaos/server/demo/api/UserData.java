package net.cofcool.chaos.server.demo.api;

import net.cofcool.chaos.server.common.security.Auth;

/**
 * 存储用户数据
 */
public class UserData implements Auth {

    private static final long serialVersionUID = 3901205826786356567L;

    private String username;

    public UserData() {
    }

    public UserData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
