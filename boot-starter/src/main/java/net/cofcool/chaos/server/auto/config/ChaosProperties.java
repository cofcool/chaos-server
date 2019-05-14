package net.cofcool.chaos.server.auto.config;

import static net.cofcool.chaos.server.auto.config.ChaosAutoConfiguration.PROJECT_CONFIGURE_PREFIX;

import java.util.Map;
import net.cofcool.chaos.server.core.annotation.ApiVersion;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.config.DevelopmentMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 项目可配置项
 *
 * @author CofCool
 */
@ConfigurationProperties(prefix = PROJECT_CONFIGURE_PREFIX)
public class ChaosProperties {

    private Auth auth = new Auth();

    private Development development = new Development();

    private Data data = new Data();

    /**
     * 应用自定义配置
     */
    private Map<String, String> properties;

    public static class Data {

        /**
         * Mybatis 包扫描路径
         */
        private String mapperPackage;

        /**
         * Mybatis xml 路径
         */
        private String xmlPath;


        public String getMapperPackage() {
            return mapperPackage;
        }

        public void setMapperPackage(String mapperPackage) {
            this.mapperPackage = mapperPackage;
        }

        public String getXmlPath() {
            return xmlPath;
        }

        public void setXmlPath(String xmlPath) {
            this.xmlPath = xmlPath;
        }
    }

    public static final class Auth {

        /**
         * 注入数据key配置, 多个时以","分隔
         */
        private String checkedKeys = "";

        /**
         * 是否开启验证码
         */
        private Boolean usingCaptcha = false;

        /**
         * 默认用户的用户名
         */
        private String defaultUsername;

        /**
         * 默认用户的密码
         */
        private String defaultPassword;

        /**
         * shiro授权路径配置
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

        public String getCheckedKeys() {
            return checkedKeys;
        }

        public void setCheckedKeys(String checkedKeys) {
            this.checkedKeys = checkedKeys;
        }

        public Boolean getUsingCaptcha() {
            return usingCaptcha;
        }

        public void setUsingCaptcha(Boolean usingCaptcha) {
            this.usingCaptcha = usingCaptcha;
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
    }


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
         * 项目运行模式
         */
        private DevelopmentMode mode = DevelopmentMode.DEV;

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

        /**
         * 项目运行模式
         *
         * @return 当前运行模式
         * @see DevelopmentMode
         */
        public DevelopmentMode getMode() {
            return mode;
        }

        public void setMode(DevelopmentMode mode) {
            this.mode = mode;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
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
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
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

    /**
     * 数据库相关配置
     */
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
