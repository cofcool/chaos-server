package net.cofcool.chaos.server.core.config;

import net.cofcool.chaos.server.common.util.StringUtils;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.springframework.util.Assert;

/**
 * 应用配置
 *
 * @author CofCool
 */
public final class WebApplicationContext {

    private ChaosProperties properties;

    private static final WebApplicationContext WEB_APPLICATION_CONTEXT = new WebApplicationContext();

    private WebApplicationContext() {}

    static void setConfiguration(ChaosProperties properties) {
        Assert.notNull(properties, "properties - this argument is required; it must not be null");
        WEB_APPLICATION_CONTEXT.properties = properties;
    }

    /**
     * 验证当前请求的"Header"是否含有"key"为{@link ChaosProperties.Auth#AUTHORIZE_KEY}的值
     * @param authorizeCode 应用所配置的authorizeCode
     * @return 是否包含
     */
    public static boolean isDevAuthorization(String authorizeCode) {
        String authorization = WebUtils.getRequest().getHeader(ChaosProperties.Auth.AUTHORIZE_KEY);
        return !StringUtils.isNullOrEmpty(authorization) && authorization.equals(authorizeCode);
    }

    /**
     * 读取应用配置
     * @return ChaosProperties
     */
    public static ChaosProperties getConfiguration() {
        return WEB_APPLICATION_CONTEXT.properties;
    }

}
