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

package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 适配 {@link AbstractLogin}
 *
 * @author CofCool
 */
public class JsonAuthenticationToken extends UsernamePasswordAuthenticationToken implements Serializable {

    private static final long serialVersionUID = 5795425390785540829L;

    private AbstractLogin login;

    public JsonAuthenticationToken(AbstractLogin login) {
        super(login.getUsername(), login.getPassword());

        Assert.notNull(login, "login - this argument is required; it must not be null");
        Assert.notNull(login.getDevice(), "login.getDevice() - this argument is required; it must not be nul");
        this.login = login;
    }

    public AbstractLogin getLogin() {
        return login;
    }

}
