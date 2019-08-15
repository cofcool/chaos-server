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

package net.cofcool.chaos.server.demo.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import net.cofcool.chaos.server.common.security.AbstractLogin;


public class Login extends AbstractLogin {

    private static final long serialVersionUID = -3868201780589666741L;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    protected net.cofcool.chaos.server.common.security.Device checkRequestAgent(HttpServletRequest servletRequest) {
       return Device.BROWSER;
    }
}
