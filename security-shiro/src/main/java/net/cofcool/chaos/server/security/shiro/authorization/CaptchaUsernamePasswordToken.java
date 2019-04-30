package net.cofcool.chaos.server.security.shiro.authorization;

import javax.annotation.Nonnull;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.Assert;

/**
 * 存储用户名密码等数据
 *
 * @author CofCool
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = -4991467222582687202L;

    private final AbstractLogin login;

    public CaptchaUsernamePasswordToken(@Nonnull AbstractLogin login) {
        super(login.getUsername(), login.getPassword());
        Assert.notNull(login, "login - this argument is required; it must not be null");
        Assert.notNull(login.getDevice(), "login.getDevice() - this argument is required; it must not be nul");
        this.login = login;
    }

    public AbstractLogin getLogin() {
        return login;
    }

}
