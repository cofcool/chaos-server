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

package net.cofcool.chaos.server.auto.config;

import java.util.Map;
import net.cofcool.chaos.server.common.security.AuthConfig;
import net.cofcool.chaos.server.core.annotation.ApiVersion;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.support.GlobalHandlerExceptionResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 项目可配置项
 *
 * @author CofCool
 */
@ConfigurationProperties(prefix = ChaosConfiguration.PROJECT_CONFIGURE_PREFIX)
public class ChaosProperties {

    private AuthConfig auth = new AuthConfig();

    private Development development = new Development();

    /**
     * 应用自定义配置
     */
    private Map<String, String> properties;


    public static final class Development {

        /**
         * 是否创建日志拦截器
         *
         * @see net.cofcool.chaos.server.core.aop.LoggingInterceptor
         */
        private Boolean loggingEnabled = false;

        /**
         * 是否创建参数验证拦截器
         *
         * @see net.cofcool.chaos.server.core.aop.ValidateInterceptor
         */
        private Boolean validatingEnabled = false;

        /**
         * 是否创建参数注入(数据隔离)拦截器
         *
         * @see net.cofcool.chaos.server.core.aop.ApiProcessingInterceptor
         */
        private Boolean injectingEnabled = false;

        /**
         * 是否调试模式
         */
        private Boolean debug = Boolean.FALSE;

        /**
         * 项目版本
         *
         * @see net.cofcool.chaos.server.core.annotation.ApiVersion
         */
        private Integer version = ApiVersion.NO_LIMIT;

        /**
         * 定义扫描 Scanned 注解的路径
         */
        private String annotationPath;

        /**
         * 是否忽略 {@link GlobalHandlerExceptionResolver} 不能处理的异常
         */
        private Boolean ignoreUnknownException = false;

        public Boolean getLoggingEnabled() {
            return loggingEnabled;
        }

        public void setLoggingEnabled(Boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
        }

        public Boolean getValidatingEnabled() {
            return validatingEnabled;
        }

        public void setValidatingEnabled(Boolean validatingEnabled) {
            this.validatingEnabled = validatingEnabled;
        }

        public Boolean getInjectingEnabled() {
            return injectingEnabled;
        }

        public void setInjectingEnabled(Boolean injectingEnabled) {
            this.injectingEnabled = injectingEnabled;
        }

        public Boolean getDebug() {
            return debug;
        }

        public void setDebug(Boolean debug) {
            this.debug = debug;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public Boolean getIgnoreUnknownException() {
            return ignoreUnknownException;
        }

        public void setIgnoreUnknownException(Boolean ignoreUnknownException) {
            this.ignoreUnknownException = ignoreUnknownException;
        }

        /**
         * 定义扫描 {@link Scanned} 注解的路径
         *
         * @return 扫描路径
         * @see Scanned
         */
        public String getAnnotationPath() {
            return annotationPath;
        }

        public void setAnnotationPath(String annotationPath) {
            this.annotationPath = annotationPath;
        }

    }

    /**
     * 授权相关配置
     */
    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

    /**
     * 开发相关配置
     */
    public Development getDevelopment() {
        return development;
    }

    public void setDevelopment(Development development) {
        this.development = development;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
