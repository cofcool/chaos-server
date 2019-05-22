package net.cofcool.chaos.server.common.core;

import java.io.Serializable;

/**
 * 封装运行结果
 *
 * @author CofCool
 */
public interface Result<T> extends Serializable {

    /**
     * 执行状态
     */
    enum ResultState {
        /**
         * 成功
         */
        SUCCESSFUL,
        /**
         * 失败
         */
        FAILURE,
        /**
         * 等待中
         */
        WAITING,
        /**
         * 未知
         */
        UNKNOWN
    }


    /**
     * 结果数据
     *
     * @return 封装的Message对象
     *
     * @see Message
     */
    Message<T> result();

    /**
     * 执行是否成功
     * @return 执行是否成功
     */
    default boolean successful() {
        return true;
    }

}
