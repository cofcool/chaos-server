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

import javax.annotation.Nonnull;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.Assert;

/**
 * 存储用户名密码等数据
 *
 * @author CofCool
 */
public class LoginUsernamePasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = -4991467222582687202L;

    private final AbstractLogin login;

    public LoginUsernamePasswordToken(@Nonnull AbstractLogin login) {
        super(login.getUsername(), login.getPassword());
        Assert.notNull(login, "login - this argument is required; it must not be null");
        Assert.notNull(login.getDevice(), "login.getDevice() - this argument is required; it must not be nul");
        this.login = login;
    }

    public AbstractLogin getLogin() {
        return login;
    }

}
