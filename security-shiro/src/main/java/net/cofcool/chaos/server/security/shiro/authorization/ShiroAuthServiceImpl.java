/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.security.shiro.authorization;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthConstant;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.AuthorizationException;
import net.cofcool.chaos.server.common.security.exception.CaptchaErrorException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

/**
 * shiro 授权管理相关处理
 *
 * @author CofCool
 */
@Slf4j
public class ShiroAuthServiceImpl<T extends Auth, ID extends Serializable> implements
    AuthService<T, ID>, InitializingBean, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    private UserAuthorizationService<T, ID> userAuthorizationService;

    private ConfigurationSupport configuration;

    protected ConfigurationSupport getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
    }

    protected UserAuthorizationService<T, ID> getUserAuthorizationService() {
        return userAuthorizationService;
    }

    public void setUserAuthorizationService(UserAuthorizationService<T, ID> userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<User<T, ID>> login(HttpServletRequest request, HttpServletResponse response, AbstractLogin loginUser) {
        try {
            loginUser.parseDevice(request);
        } catch (CaptchaErrorException e) {
            log.trace("captcha error", e);
            publishEvent(loginUser, e);
            return configuration
                .getMessage(
                    e.getCode(),
                    e.getMessage(),
                    null
                );
        }
        LoginUsernamePasswordToken token = new LoginUsernamePasswordToken(loginUser);

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
                    return configuration.getMessage(checkedMessage.code(), checkedMessage.message(), null);
                }

                isOk = true;
                storageUser(currentUser);

                publishEvent(currentUser, null);
                return returnUserInfo(currentUser);
            }

            return getExceptionMessage(ExceptionCodeDescriptor.AUTH_ERROR, ExceptionCodeDescriptor.AUTH_ERROR_DESC);
        } catch (UnknownAccountException e) {
            publishEvent(loginUser, e);

            return getExceptionMessage(ExceptionCodeDescriptor.USER_NOT_EXITS, ExceptionCodeDescriptor.USER_NOT_EXITS_DESC);
        } catch (IncorrectCredentialsException e) {
            publishEvent(loginUser, e);

            return getExceptionMessage(ExceptionCodeDescriptor.USER_PASSWORD_ERROR, ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC);
        } catch (AuthenticationException e) {
            publishEvent(loginUser, e);

            Throwable ex = e.getCause();
            if (ex instanceof AuthorizationException) {
                return configuration.getMessage(((AuthorizationException) ex).getCode(), ex.getMessage(), null);
            } else {
                return configuration.getMessage(
                    ExceptionCodeDescriptor.AUTH_ERROR,
                    true,
                    e.getMessage(),
                    false,
                    null
                );
            }
        } catch (Exception e) {
            publishEvent(loginUser, e);
            return getExceptionMessage(ExceptionCodeDescriptor.AUTH_ERROR, ExceptionCodeDescriptor.AUTH_ERROR_DESC);
        } finally {
            if (!isOk) {
                user.logout();
            }
        }
    }

    protected Message<User<T, ID>> getExceptionMessage(String code, String type) {
        return configuration.getMessageWithKey(
            code,
            type,
            null
        );
    }

    private void publishEvent(Object loginUser, Exception e) {
        applicationEventPublisher.publishEvent(new ShiroApplicationEvent(loginUser, e));
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
        return configuration.getMessageWithKey(
            ExceptionCodeDescriptor.SERVER_OK,
            ExceptionCodeDescriptor.SERVER_OK_DESC,
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
