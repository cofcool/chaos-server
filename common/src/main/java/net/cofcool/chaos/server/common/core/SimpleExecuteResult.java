package net.cofcool.chaos.server.common.core;

import java.util.NoSuchElementException;
import java.util.Objects;

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


    public SimpleExecuteResult(ResultState state, Message<T> message) {
        this.entity = message.getData();
        this.state = state;
        this.message = message;
    }

    @Override
    public T getEntity() {
        if (!successful() || entity == null) {
            throw new NoSuchElementException("No value present");
        }

        return entity;
    }

    @Override
    public ResultState getState() {
        return state;
    }

    @Override
    public T orElse(T newVal) {
        return entity == null ? newVal : entity;
    }

    public void setMessage(Message<T> message) {
        Objects.requireNonNull(message);
        this.message = message;
        this.entity = message.getData();
    }

    @Override
    public Message<T> getResult() {
        return message;
    }
}
