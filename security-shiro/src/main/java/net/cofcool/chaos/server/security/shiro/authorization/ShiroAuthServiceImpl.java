package net.cofcool.chaos.server.security.shiro.authorization;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthConstant;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.AuthorizationException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * shiro 授权管理相关处理
 *
 * @author CofCool
 */
public class ShiroAuthServiceImpl<T extends Auth, ID extends Serializable> implements
    AuthService<T, ID>, InitializingBean {

    private UserAuthorizationService<T, ID> userAuthorizationService;

    private ExceptionCodeManager exceptionCodeManager;

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    public void setExceptionCodeManager(
        ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    public UserAuthorizationService<T, ID> getUserAuthorizationService() {
        return userAuthorizationService;
    }

    public void setUserAuthorizationService(UserAuthorizationService<T, ID> userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
        this.userAuthorizationService.setUserProcessor(this::storageUser);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<User<T, ID>> login(AbstractLogin loginUser) {
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

                Message<Boolean> checkedMessage = getUserAuthorizationService().checkUser(currentUser);
                if (checkedMessage.data()) {
                    getUserAuthorizationService().setupUserData(currentUser);
                } else {
                    user.logout();
                    return Message.of(checkedMessage.code(), checkedMessage.message());
                }

                isOk = true;
                storageUser(currentUser);
                return returnUserInfo(currentUser);
            }

            return getExceptionMessage(ExceptionCodeDescriptor.AUTH_ERROR, ExceptionCodeDescriptor.AUTH_ERROR_DESC);
        } catch (UnknownAccountException e) {
            reportException(loginUser, e);

            return getExceptionMessage(ExceptionCodeDescriptor.USER_NOT_EXITS, ExceptionCodeDescriptor.USER_NOT_EXITS_DESC);
        } catch (IncorrectCredentialsException e) {
            reportException(loginUser, e);

            return getExceptionMessage(ExceptionCodeDescriptor.USER_PASSWORD_ERROR, ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC);
        } catch (AuthenticationException e) {
            reportException(loginUser, e);

            Throwable ex = e.getCause();
            if (ex instanceof AuthorizationException) {
                return Message.of(((AuthorizationException) ex).getCode(), ex.getMessage());
            } else {
                return Message.of(
                    exceptionCodeManager.getCode(ExceptionCodeDescriptor.AUTH_ERROR),
                    e.getMessage()
                );
            }
        } catch (Exception e) {
            reportException(loginUser, e);
            return getExceptionMessage(ExceptionCodeDescriptor.AUTH_ERROR, ExceptionCodeDescriptor.AUTH_ERROR_DESC);
        } finally {
            if (!isOk) {
                user.logout();
            }
        }
    }

    protected Message<User<T, ID>> getExceptionMessage(String code, String type) {
        return Message.of(
            exceptionCodeManager.getCode(code),
            exceptionCodeManager.getDescription(type)
        );
    }

    private void reportException(AbstractLogin loginUser, Exception e) {
        getUserAuthorizationService().reportAuthenticationExceptionInfo(loginUser ,e);
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SecurityUtils.getSubject().logout();
    }

    private void setupBaseDataOfUser(User currentUser, AbstractLogin loginUser) {
        currentUser.setDevice(loginUser.getDevice());
    }

    /**
     * 存储用户数据, 会覆盖旧数据, 谨慎使用
     * @param currentUser 用户信息
     */
    protected void storageUser(User currentUser) {
        SecurityUtils.getSubject().getSession().setAttribute(AuthConstant.LOGINED_USER_KEY, currentUser);
    }

    @SuppressWarnings("unchecked")
    private User<T, ID> getCachedUser() {
        Session session = SecurityUtils.getSubject().getSession(false);
        if (session != null) {
            return (User<T, ID>) session.getAttribute(AuthConstant.LOGINED_USER_KEY);
        }

        return null;
    }

    private Message<User<T, ID>> returnUserInfo(User<T, ID> user) {
        return Message.of(
            exceptionCodeManager.getCode(ExceptionCodeDescriptor.SERVER_OK),
            exceptionCodeManager.getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC),
            user
        );
    }

    @Override
    public User<T, ID> readCurrentUser() {
        return getCachedUser();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getUserAuthorizationService(), "userAuthorizationService - this argument is required; it must not be null");
    }
}
