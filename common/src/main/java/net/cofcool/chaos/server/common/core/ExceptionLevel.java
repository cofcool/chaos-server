package net.cofcool.chaos.server.common.core;

/**
 * 异常等级
 *
 * @author CofCool
 */
public interface ExceptionLevel {

    /**
     * 最高级别
     */
    int HIGHEST_LEVEL = Integer.MIN_VALUE;

    /**
     * 最低级别
     */
    int LOWEST_LEVEL = Integer.MAX_VALUE;

    /**
     * 普通等级，高于该等级的异常会输出日志
     */
    int NORMAL_LEVEL = 0;

    int getLevel();

    /**
     * 是否输出日志，true则表示输出日志
     * @return 是否输出日志
     */
    default boolean showable() {
        return getLevel() < NORMAL_LEVEL;
    }

}
