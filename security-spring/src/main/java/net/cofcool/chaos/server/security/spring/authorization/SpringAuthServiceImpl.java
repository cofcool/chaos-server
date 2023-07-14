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

package net.cofcool.chaos.server.security.spring.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * Spring Security 授权管理相关处理, 不支持 {@link #login(HttpServletRequest, HttpServletResponse, AbstractLogin)}, 登录操作由 {@link JsonAuthenticationFilter} 完成
 *
 * @author CofCool
 *
 * @see AuthService
 * @see UserDetailsService
 * @see UserDetailsManager
 */
@Slf4j
public class SpringAuthServiceImpl<T extends Auth, ID extends Serializable> implements AuthService<T, ID> {


    @Override
    public Message<User<T, ID>> login(HttpServletRequest request, HttpServletResponse response, AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        new SecurityContextLogoutHandler()
                .logout(httpServletRequest, httpServletResponse, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public User<T, ID> readCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (authentication.isAuthenticated() && principal instanceof User) {
                return (User<T, ID>) principal;
            }
        }

        return null;
    }

}
