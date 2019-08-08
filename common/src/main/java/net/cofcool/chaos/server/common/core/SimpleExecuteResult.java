/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        this.entity = message.data();
        this.state = state;
        this.message = message;
    }

    @Override
    public T entity() {
        if (!successful() || entity == null) {
            throw new NoSuchElementException("No value present");
        }

        return entity;
    }

    @Override
    public ResultState state() {
        return state;
    }

    @Override
    public T orElse(T newVal) {
        return entity == null ? newVal : entity;
    }

    public void setMessage(Message<T> message) {
        Objects.requireNonNull(message);
        this.message = message;
        this.entity = message.data();
    }

    @Override
    public Message<T> result() {
        return message;
    }
}
