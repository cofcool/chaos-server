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

package net.cofcool.chaos.server.security.shiro.access;

import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.common.security.exception.LoginException;
import net.cofcool.chaos.server.common.security.exception.UserNotExistException;
import net.cofcool.chaos.server.security.shiro.authorization.LoginUsernamePasswordToken;
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
@SuppressWarnings("rawtypes")
public class AuthRealm extends AuthorizingRealm implements InitializingBean {

    private UserAuthorizationService userAuthorizationService;

    private ConfigurationSupport configuration;

    public UserAuthorizationService getUserAuthorizationService() {
        return userAuthorizationService;
    }

    public void setUserAuthorizationService(
        UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    public ConfigurationSupport getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
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
        LoginUsernamePasswordToken token = (LoginUsernamePasswordToken) authcToken;

        User user = getUserAuthorizationService().queryUser(token.getLogin());

        if (user == null) {
            throw new UserNotExistException(
                getExceptionDesc(ExceptionCodeDescriptor.USER_NOT_EXITS_DESC),
                getExceptionCode(ExceptionCodeDescriptor.USER_NOT_EXITS)
            );
        }

        if (user.getUserStatuses().contains(UserStatus.LOCKED) || user.getUserStatuses().contains(UserStatus.CANCEL)) {
            throw new LoginException(
                getExceptionDesc(ExceptionCodeDescriptor.DENIAL_AUTH_DESC),
                getExceptionCode(ExceptionCodeDescriptor.DENIAL_AUTH)
            );
        }

        return new SimpleAuthenticationInfo(user, token, this.getName());
    }

    private String getExceptionCode(String type) {
        return configuration.getExceptionCode(type);
    }

    private String getExceptionDesc(String type) {
        return configuration.getExceptionDescription(type);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getUserAuthorizationService(), "userAuthorizationService - this argument is required; it must not be null");
    }
}

