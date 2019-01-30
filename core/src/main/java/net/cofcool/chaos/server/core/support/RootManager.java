package net.cofcool.chaos.server.core.support;

import net.cofcool.chaos.server.core.config.WebApplicationContext;

/**
 * 管理Root用户密码和用户名
 *
 * @author CofCool
 */
public class RootManager {

    public static String password() {
        return WebApplicationContext.getConfiguration().getAuth().getDefaultPassword();
    }

    public static String username() {
        return WebApplicationContext.getConfiguration().getAuth().getDefaultUsername();
    }

}