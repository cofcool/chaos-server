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

import java.util.Objects;

/**
 * {@link QueryResult} 简单实现
 *
 * @author CofCool
 */
public class SimpleQueryResult<T> implements QueryResult<T, Page<T>> {

    private static final long serialVersionUID = 3886864123428767070L;

    private final Message<Page<T>> message;

    /**
     * 查询结果封装
     *
     * @param message 分页数据
     */
    public SimpleQueryResult(Message<Page<T>> message) {
        Objects.requireNonNull(message.data());
        Objects.requireNonNull(message.data().getContent());

        this.message = message;
    }

    @Override
    public Page<T> page() {
        return message.data();
    }

    @Override
    public Message<Page<T>> result() {
        return message;
    }

    @Override
    public String toString() {
        return "SimpleQueryResult{" +
            "message=" + message +
            '}';
    }
}
