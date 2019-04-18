package net.cofcool.chaos.server.common.core;


/**
 * Message 的简单实现
 *
 * @author CofCool
 */
public class SimpleMessage<T> implements Message<T> {

    private static final long serialVersionUID = -5993515594721000852L;

    private String code;

    private String message;

    private T data;

    public SimpleMessage() {

    }

    public SimpleMessage(String code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
