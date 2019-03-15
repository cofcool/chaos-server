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
    String getCode();

    /**
     * 描述信息
     */
    String getMessage();

    /**
     * 携带数据
     */
    T getData();


    static <T> Message<T> of(String code, String msg, T data) {
        return new SimpleMessage<>(code, msg, data);
    }

    static <T> Message<T> successful(String msg, T data) {
        return SimpleMessage.successful(msg, data);
    }

    static<T> Message<T>  error(String code, String msg, T data) {
        return SimpleMessage.error(code, msg, data);
    }

    static<T> Message<T> error(String code, String msg) {
        return SimpleMessage.error(code, msg);
    }

}
