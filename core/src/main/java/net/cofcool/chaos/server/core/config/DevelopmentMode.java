package net.cofcool.chaos.server.core.config;

/**
 * 项目开发模式
 *
 * @author CofCool
 */
public enum DevelopmentMode {
    /**
     * 开发模式
     */
    DEV,
    /**
     * 测试模式
     */
    TEST,
    /**
     * 发布模式
     */
    RELEASE;

    /**
     * 是否是调试模式, 在 {@link #DEV} 和 {@link #TEST} 模式下会处于调试模式
     * @return 是否是调试模式
     */
    public boolean isDebugMode() {
        return this.equals(DevelopmentMode.DEV) ||
                this.equals(DevelopmentMode.TEST);
    }


}
