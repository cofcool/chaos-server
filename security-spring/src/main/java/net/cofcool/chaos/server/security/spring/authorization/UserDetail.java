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

import java.util.Collection;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.security.spring.support.UserUtils;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户数据
 *
 * @author CofCool
 */
public class UserDetail extends User implements
    UserDetails, CredentialsContainer {

    private static final long serialVersionUID = -634235060622704818L;

    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getUserStatuses().contains(UserStatus.LOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return getUserStatuses().contains(UserStatus.NORMAL);
    }

    /**
     * 根据 User 创建 UserDetail 实例
     * @param user user 实例
     * @return UserDetail
     */
    @SuppressWarnings("unchecked")
    public static UserDetails of(User user) {
        if (user instanceof UserDetails) {
            return (UserDetails) user;
        }

        UserDetail userDetail = new UserDetail();
        user.cloneUser(userDetail);
        // 转换角色为 Spring 的 GrantedAuthority 类型
        userDetail.setRoles(UserUtils.convertUserRolesForSpring(user.getRoles()));
        userDetail.setAccountNonExpired(true);
        userDetail.setCredentialsNonExpired(true);

        return userDetail;
    }

    @Override
    public void eraseCredentials() {
        setPassword(null);
    }

}
