package net.cofcool.chaos.server.common.core;

import java.util.NoSuchElementException;

/**
 * ExecuteResult 简单实现
 *
 * @author CofCool
 */
public class SimpleExecuteResult<T> implements ExecuteResult<T> {

    private static final long serialVersionUID = 345300521174121626L;

    private T entity;

    private ResultState state;

    private Message<T> message;

    public SimpleExecuteResult(T entity, ResultState state) {
        this(entity, state, null);
    }

    public SimpleExecuteResult(T entity, ResultState state, Message<T> message) {
        this.entity = entity;
        this.state = state;
        this.message = message;
    }

    public SimpleExecuteResult(ResultState state, Message<T> message) {
        this(message.getData(), state, message);
    }

    @Override
    public T getEntity() {
        if (!successful() || entity == null) {
            throw new NoSuchElementException("No value present");
        }

        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public ResultState getState() {
        return state;
    }

    public void setState(ResultState state) {
        this.state = state;
    }

    @Override
    public Message<T> getMessage() {
        return message;
    }

    @Override
    public T orElse(T newVal) {
        return entity == null ? newVal : entity;
    }

    public void setMessage(Message<T> message) {
        this.message = message;
    }

    @Override
    public Message<T> getResult(String message) {
        if (getMessage() != null) {
            return getMessage();
        }

        if (successful()) {
            return Message.successful(message, getEntity());
        } else {
            return Message.error(ExceptionCode.OPERATION_ERR, message, null);
        }
    }
}
