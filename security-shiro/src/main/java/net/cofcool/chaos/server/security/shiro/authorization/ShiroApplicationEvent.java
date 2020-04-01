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

package net.cofcool.chaos.server.security.shiro.authorization;

import javax.annotation.Nullable;
import org.springframework.context.ApplicationEvent;

/**
 * Shiro 相关的事件, 如果登录成功, <code>source</code> 为 {@link net.cofcool.chaos.server.common.security.User}, 否则为 {@link net.cofcool.chaos.server.common.security.AbstractLogin}
 */
public class ShiroApplicationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3855890031839833100L;

    private final Exception shiroException;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with which the event is
     *               associated (never {@code null})
     */
    public ShiroApplicationEvent(Object source, Exception shiroException) {
        super(source);
        this.shiroException = shiroException;
    }

    /**
     * 如果登录成功, 返回 <code>null</code>
     * @return 登录相关异常
     */
    @Nullable
    public Exception getShiroException() {
        return shiroException;
    }
}
