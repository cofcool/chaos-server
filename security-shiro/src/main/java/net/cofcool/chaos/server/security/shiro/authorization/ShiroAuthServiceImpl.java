package net.cofcool.chaos.server.security.shiro.authorization;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthConstant;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.AuthUserService;
import net.cofcool.chaos.server.common.security.authorization.exception.CaptchaException;
import net.cofcool.chaos.server.common.security.authorization.exception.LoginException;
import net.cofcool.chaos.server.common.security.authorization.exception.UserNotExistException;
import net.cofcool.chaos.server.core.support.ExceptionCodeInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 授权管理抽象类
 *
 * @author CofCool
 */

@Slf4j
public class ShiroAuthServiceImpl<T extends Auth<D, ID>, D extends Serializable, ID extends Serializable> implements AuthService<T, D, ID>, InitializingBean {

    private AuthUserService<T, D, ID> authUserService;

    public AuthUserService<T, D, ID> getAuthUserService() {
        return authUserService;
    }

    public void setAuthUserService(AuthUserService<T, D, ID> authUserService) {
        this.authUserService = authUserService;
        this.authUserService.setUserProcessor(this::storageUser);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<User<T, D, ID>> login(AbstractLogin loginUser) {
        CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(loginUser);

        Subject user = SecurityUtils.getSubject();


        if (user.getPrincipal() != null) {
            user.logout();
        }

        boolean isOk = false;

        try {
            user.login(token);

            User currentUser = (User) user.getPrincipal();

            if (currentUser != null) {
                setupBaseDataOfUser(currentUser, loginUser);

                Message<Boolean> checkedMessage = authUserService.checkUser(currentUser);
                if (checkedMessage.getData()) {
                    authUserService.setupUserData(currentUser);
                } else {
                    user.logout();
                    return Message.error(checkedMessage.getCode(), checkedMessage.getMessage());
                }

                isOk = true;
                storageUser(currentUser);
                return returnUserInfo(currentUser);
            }

            return Message.error(ExceptionCode.SERVER_ERR, ExceptionCodeInfo.serverError());
        } catch (UnknownAccountException e) {
            return Message.error(ExceptionCode.DENIAL_AUTH, ExceptionCodeInfo.usernameError());
        } catch (IncorrectCredentialsException e) {
            return Message.error(ExceptionCode.DENIAL_AUTH, ExceptionCodeInfo.userPasswordError());
        } catch (AuthenticationException e) {
            Throwable ex = e.getCause();
            if (ex instanceof CaptchaException || ex instanceof UserNotExistException
                || ex instanceof LoginException) {
                return Message.error(ExceptionCode.DENIAL_AUTH, ex.getMessage());
            } else if (ex instanceof ServiceException) {
                return Message.error(((ServiceException)ex).getCode() == null ? ExceptionCode.DENIAL_AUTH : ((ServiceException)ex).getCode(), e.getMessage());
            } else {
                return Message.error(ExceptionCode.NO_LOGIN, ExceptionCodeInfo.noLogin());
            }
        }
        catch (Exception e) {
            log.error("auth: other exception", e.getMessage());
            return Message.error(ExceptionCode.SERVER_ERR, ExceptionCodeInfo.serverError());
        } finally {
            if (!isOk) {
                user.logout();
            }
        }
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SecurityUtils.getSubject().logout();
    }

    private void setupBaseDataOfUser(User currentUser, AbstractLogin loginUser) {
        currentUser.setDevice(loginUser.getDevice());
    }

    /**
     * 存储用户数据，会覆盖旧数据，谨慎使用
     * @param currentUser 用户信息
     */
    protected void storageUser(User currentUser) {
        SecurityUtils.getSubject().getSession().setAttribute(AuthConstant.LOGINED_USER_KEY, currentUser);
    }

    @SuppressWarnings("unchecked")
    private User<T, D, ID> getCachedUser() {
        Session session = SecurityUtils.getSubject().getSession(false);
        if (session != null) {
            return (User<T, D, ID>) session.getAttribute(AuthConstant.LOGINED_USER_KEY);
        }

        return null;
    }

    private Message<User<T, D, ID>> returnUserInfo(User<T, D, ID> user) {
        return Message.successful(ExceptionCodeInfo.serverOk(), user);
    }

    @Override
    public boolean checkPermission(ServletRequest servletRequest, ServletResponse servletResponse) {
        return authUserService.checkPermission(servletRequest, servletResponse);
    }

    @Override
    public User<T, D, ID> readCurrentUser() {
        return getCachedUser();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getAuthUserService(), "authUserService - this argument is required; it must not be null");
    }
}
