package net.cofcool.chaos.server.security.shiro.access;

import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.security.shiro.authorization.CaptchaUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * 密码验证, 配合 {@link CaptchaUsernamePasswordToken}
 *
 * @author CofCool
 */
public class AccountCredentialsMatcher implements CredentialsMatcher {

    private PasswordProcessor passwordProcessor;

    public PasswordProcessor getPasswordProcessor() {
        return passwordProcessor;
    }

    public void setPasswordProcessor(
        PasswordProcessor passwordProcessor) {
        this.passwordProcessor = passwordProcessor;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (!(token instanceof CaptchaUsernamePasswordToken)) {
            return false;
        }

        CaptchaUsernamePasswordToken userToken = (CaptchaUsernamePasswordToken) token;
        User user = (User) info.getPrincipals().getPrimaryPrincipal();
        return passwordProcessor.doMatch(new String(userToken.getPassword()), user.getLoginPwd());
    }
}
