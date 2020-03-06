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

import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.security.shiro.authorization.LoginUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * 密码验证, 配合 {@link LoginUsernamePasswordToken}
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
        if (!(token instanceof LoginUsernamePasswordToken)) {
            return false;
        }

        LoginUsernamePasswordToken userToken = (LoginUsernamePasswordToken) token;
        User user = (User) info.getPrincipals().getPrimaryPrincipal();
        return passwordProcessor.doMatch(new String(userToken.getPassword()), user.getPassword());
    }
}
