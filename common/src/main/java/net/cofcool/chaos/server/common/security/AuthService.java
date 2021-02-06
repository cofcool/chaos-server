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

package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.Message;

/**
 * 授权管理
 *
 * @param <T> User中用户详细数据
 * @param <ID> 用户id
 *
 * @author CofCool
 */
public interface AuthService<T extends Auth, ID extends Serializable> {

    /**
     * 登陆
     * @param loginUser 登陆时携带的参数
     * @return 登陆数据
     */
    default Message<User<T, ID>> login(HttpServletRequest request, HttpServletResponse response, AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    /**
     * 退出登陆
     */
    default void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        throw new UnsupportedOperationException();
    }

    /**
     * 读取当前登陆用户数据
     * @return {@link User}
     */
    @Nullable
    User<T, ID> readCurrentUser();

}
