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

import net.cofcool.chaos.server.common.util.StringUtils;

/**
 * 鉴权相关配置
 * @author CofCool
 */
public class AuthConfig {

    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 注入数据key配置, 多个时以","分隔
     */
    private String checkedKeys = "";

    /**
     * 默认用户的用户名
     */
    private String defaultUsername;

    /**
     * 默认用户的密码
     */
    private String defaultPassword;

    /**
     * 授权路径配置, 用";"分割
     * <br>
     * <b>注意</b>: "Spring Security" 项目, 该配置为匿名访问路径; "Shiro" 为授权路径配置, 即"filterChainDefinitions"
     */
    private String urls;

    /**
     * 登陆路径
     */
    private String loginUrl = "/auth/login";

    /**
     * 退出登陆路径
     */
    private String logoutUrl = "/auth/logout";

    /**
     * 登陆过期路径
     */
    private String expiredUrl = "/auth/unlogin";

    /**
     * 没有访问权限路径
     */
    private String unauthUrl = "/auth/unauth";

    /**
     * 未登录时跳转路径
     */
    private String unLoginUrl = expiredUrl;

    /**
     * 是否启用 <code>org.springframework.web.filter.CorsFilter</code>, 默认关闭
     */
    private Boolean corsEnabled = false;


    /**
     * 是否启用 <code>org.springframework.security.web.csrf.CsrfFilter</code> , 默认启用
     */
    private Boolean csrfEnabled = true;

    /**
     * 配置 {@link net.cofcool.chaos.server.common.security.UserAuthorizationService#queryUser(AbstractLogin)} 参数的类型
     */
    private Class<? extends AbstractLogin> loginObjectType;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Class<? extends AbstractLogin> getLoginObjectType() {
        return loginObjectType;
    }

    public void setLoginObjectType(
        Class<? extends AbstractLogin> loginObjectType) {
        this.loginObjectType = loginObjectType;
    }

    public Boolean getCorsEnabled() {
        return corsEnabled;
    }

    public void setCorsEnabled(Boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
    }

    public Boolean getCsrfEnabled() {
        return csrfEnabled;
    }

    public void setCsrfEnabled(Boolean csrfEnabled) {
        this.csrfEnabled = csrfEnabled;
    }

    public String getCheckedKeys() {
        return checkedKeys;
    }

    public void setCheckedKeys(String checkedKeys) {
        this.checkedKeys = checkedKeys;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getDefaultUsername() {
        return defaultUsername;
    }

    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getExpiredUrl() {
        return expiredUrl;
    }

    public void setExpiredUrl(String expiredUrl) {
        this.expiredUrl = expiredUrl;
    }

    public String getUnauthUrl() {
        return unauthUrl;
    }

    public void setUnauthUrl(String unauthUrl) {
        this.unauthUrl = unauthUrl;
    }

    public String getUnLoginUrl() {
        return unLoginUrl;
    }

    public void setUnLoginUrl(String unLoginUrl) {
        this.unLoginUrl = unLoginUrl;
    }

    /**
     * 为 "Spring Security" 配置匿名访问路径
     * @return 匿名访问路径
     */
    public String springExcludeUrl() {
        if (StringUtils.isNullOrEmpty(urls)) {
            return String.join(
                ",",
                getUnauthUrl(),
                getExpiredUrl(),
                getUnLoginUrl(),
                getLogoutUrl()
            );
        } else {
            return String.join(
                ",",
                getUrls().replace(";", ","),
                getUnauthUrl(),
                getExpiredUrl(),
                getUnLoginUrl(),
                getLogoutUrl()
            );
        }
    }

    /**
     * 为 "Shiro" 配置授权路径, 即"filterChainDefinitions"
     * @return 授权路径字符串
     */
    public String shiroUrls() {
        StringBuilder urlStr = new StringBuilder();
        String[] urls = getUrls().split(";");
        for (String url : urls) {
            urlStr.append(url).append("\n");
        }

        return urlStr.toString();
    }
}
