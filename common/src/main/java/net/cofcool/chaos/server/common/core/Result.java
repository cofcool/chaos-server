package net.cofcool.chaos.server.common.core;

import java.io.Serializable;

/**
 * 封装运行结果
 *
 * @author CofCool
 */
public interface Result<T> extends Serializable {

    /**
     * 执行成功，只要大于或等于该值
     */
    int EXECUTE_STATE_SUCCESSFUL = 1;

    /**
     * 执行失败
     */
    int EXECUTE_STATE_FAILURE = 0;


    /**
     * 结果数据
     *
     * @param message 描述信息
     * @return 封装的Message对象
     *
     * @see Message
     */
    Message<T> getResult(String message);

    /**
     * 执行是否成功
     * @return 执行是否成功
     */
    default boolean successful() {
        return true;
    }

}
