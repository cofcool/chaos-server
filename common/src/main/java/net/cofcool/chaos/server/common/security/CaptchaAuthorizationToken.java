package net.cofcool.chaos.server.common.security;

import java.io.Serializable;

/**
 * 验证码用户名密码token
 *
 **/
public interface CaptchaAuthorizationToken extends Serializable {

    Integer getRoleId();

    String getModel();

    String getUsername();

    char[] getPassword();

    String getCaptcha();

}
