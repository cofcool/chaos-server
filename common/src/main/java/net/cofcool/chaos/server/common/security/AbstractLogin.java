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

package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import net.cofcool.chaos.server.common.security.exception.CaptchaErrorException;

/**
 * 登陆数据
 *
 * @author CofCool
 */
public abstract class AbstractLogin implements Serializable {


    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 请求设备
     */
    private Device device;


    public AbstractLogin() {
    }

    public AbstractLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 解析请求设备
     * @param servletRequest 请求
     * @throws CaptchaErrorException 验证码验证不通过时抛出该异常, 查看 {@link #checkCaptcha(HttpServletRequest)}
     */
    public void parseDevice(HttpServletRequest servletRequest) {
        checkCaptcha(servletRequest);
        setDevice(checkRequestAgent(servletRequest));
    }

    /**
     * 解析请求设备
     * @param servletRequest 请求
     * @return 设备数据
     */
    protected abstract Device checkRequestAgent(HttpServletRequest servletRequest);

    /**
     * 检查验证码
     * @param servletRequest 请求
     * @throws CaptchaErrorException 验证不通过时抛出该异常
     */
    protected void checkCaptcha(HttpServletRequest servletRequest) {
    }

    public Device getDevice() {
        return device;
    }

    protected void setDevice(Device device) {
        this.device = device;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AbstractLogin{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", device=" + device +
            '}';
    }

    /**
     * 默认登陆实现
     */
    public static class DefaultLogin extends AbstractLogin {

        private static final long serialVersionUID = -1025027878325152656L;

        public DefaultLogin(String username, String password) {
            super(username, password);
        }

        public DefaultLogin() {
        }

        @Override
        protected Device checkRequestAgent(HttpServletRequest servletRequest) {
            return SimpleDevice.BROWSER;
        }

    }


}
