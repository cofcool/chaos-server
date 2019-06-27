package net.cofcool.chaos.server.common.core;


/**
 * Message 的简单实现
 *
 * @author CofCool
 */
public class SimpleMessage<T> implements Message<T> {

    private static final long serialVersionUID = -5993515594721000852L;

    /**
     * 状态码
     */
    private String code;

    /**
     * 描述信息
     */
    private String message;

    /**
     * 携带数据
     */
    private T data;

    public SimpleMessage() {

    }

    public SimpleMessage(String code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public T data() {
        return data;
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }

}
