package net.cofcool.chaos.server.common.core;

import java.io.Serializable;

/**
 * 封装数据, 包含状态与描述
 *
 * @author CofCool
 */
public interface Message<T> extends Serializable {

    /**
     * 状态码
     */
    String code();

    /**
     * 描述信息
     */
    String message();

    /**
     * 携带数据
     */
    T data();


    /**
     * 创建 Message 实例的方便方法
     * @param code 状态码
     * @param msg 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return Message 实例
     */
    static <T> Message<T> of(String code, String msg, T data) {
        return new SimpleMessage<>(code, msg, data);
    }

    /**
     * 创建 Message 实例的方便方法
     * @param code 状态码
     * @param msg 描述信息
     * @param <T> 携带数据类型
     * @return Message 实例
     */
    static<T> Message<T> of(String code, String msg) {
        return new SimpleMessage<>(code, msg, null);
    }

}
