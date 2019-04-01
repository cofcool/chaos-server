package net.cofcool.chaos.server.security.shiro.access;

import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.security.AuthConstant;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.common.security.authorization.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.authorization.exception.CaptchaException;
import net.cofcool.chaos.server.common.security.authorization.exception.LoginException;
import net.cofcool.chaos.server.common.security.authorization.exception.UserNotExistException;
import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.core.support.ExceptionCodeManager;
import net.cofcool.chaos.server.security.shiro.authorization.CaptchaUsernamePasswordToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


@Slf4j
public class AuthRealm extends AuthorizingRealm implements InitializingBean {

    private UserAuthorizationService userAuthorizationService;

    private ExceptionCodeManager exceptionCodeManager;

    /**
     * 是否使用验证码
     */
    private boolean usingCaptcha = false;

    public boolean isUsingCaptcha() {
        return usingCaptcha;
    }

    public void setUsingCaptcha(boolean usingCaptcha) {
        this.usingCaptcha = usingCaptcha;
    }

    public UserAuthorizationService getUserAuthorizationService() {
        return userAuthorizationService;
    }

    public void setUserAuthorizationService(
        UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    public void setExceptionCodeManager(ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    /**
     * 权限验证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }


    /**
     * 登录验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        CaptchaUsernamePasswordToken token = (CaptchaUsernamePasswordToken) authcToken;

        checkCaptcha(token);

        User user = getUserAuthorizationService().queryUser(token.getLogin());

        if (user == null) {
            throw new UserNotExistException(exceptionCodeManager.get(ExceptionCode.USER_NOT_EXITS_KEY, true));
        }

        if (user.getUserStatuses().contains(UserStatus.LOCKED) || user.getUserStatuses().contains(UserStatus.CANCEL)) {
            throw new LoginException(exceptionCodeManager.get(ExceptionCode.DENIAL_AUTH_KEY, true));
        }

        return new SimpleAuthenticationInfo(user, token, this.getName());
    }

    private void checkCaptcha(CaptchaUsernamePasswordToken token) {
        if (isUsingCaptcha() && token.getLogin().getDevice().shouldValidate()) {
            String realCaptcha = (String) SecurityUtils.getSubject().getSession().getAttribute(AuthConstant.CAPTCHA_CODE_KEY);
            String captcha = token.getLogin().getCode();

            if (StringUtils.isNullOrEmpty(captcha) || !captcha.equalsIgnoreCase(realCaptcha)) {
                throw new CaptchaException(exceptionCodeManager.get(ExceptionCode.CAPTCHA_ERROR_KEY, true));
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getUserAuthorizationService(), "userAuthorizationService - this argument is required; it must not be null");
    }
}

