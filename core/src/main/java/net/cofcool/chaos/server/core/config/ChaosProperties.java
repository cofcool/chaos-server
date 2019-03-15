package net.cofcool.chaos.server.core.config;

import java.io.File;
import net.cofcool.chaos.server.core.annotation.Scanned;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * 项目可配置项
 *
 * @author CofCool
 */
@ConfigurationProperties(prefix = ChaosConfiguration.PROJECT_CONFIGURE_PREFIX)
public final class ChaosProperties {

    /**
     * 项目配置文件路径
     */
    private String configureDir = "";

    private Auth auth = new Auth();

    private Development development = new Development();

    private Data data = new Data();

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

        public static final String AUTHORIZE_KEY = "authorization";

        /**
         * 注入数据key配置, 多个时以","分隔
         */
        private String checkedKeys = "";

        /**
         * 授权码
         */
        private String authorizeCode = "";

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
         * 登陆
         */
        private String loginUrl = "/auth/login";

        /**
         * 退出登陆
         */
        private String logoutUrl = "/auth/logout";

        /**
         * 登陆过期
         */
        private String expiredUrl = "/auth/unlogin";

        /**
         * 没有访问权限
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

        /**
         * 授权码, 参考 {@link WebApplicationContext#isDevAuthorization(String)}
         */
        public String getAuthorizeCode() {
            return authorizeCode;
        }

        public void setAuthorizeCode(String authorizeCode) {
            this.authorizeCode = authorizeCode;
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
         * 项目运行模式
         */
        private DevelopmentMode mode = DevelopmentMode.DEV;

        /**
         * 项目版本
         */
        private Integer version;

        /**
         * 定义扫描 Scanned 注解的路径
         */
        private String annotationPath;

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

    public String getConfigureDir() {
        return configureDir;
    }

    public void setConfigureDir(String configureDir) {
        this.configureDir = configureDir;
    }

    /**
     * 项目配置文件路径, 相对路径则在 ${user.home} 目录下
     * @return 配置文件路径
     */
    public String getBaseConfigureDirectory() {
        return getConfigureDir().startsWith(File.separator) ?
            getConfigureDir() :
            System.getProperty("user.home") + File.separator + getConfigureDir();
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

    public boolean isDev() {
        return getDevelopment().getMode().equals(DevelopmentMode.DEV) ||
                getDevelopment().getMode().equals(DevelopmentMode.TEST);
    }

    public String[] getDefinedAuthDataKeys() {
        return StringUtils.delimitedListToStringArray(getAuth().getCheckedKeys(), ",");
    }

}
