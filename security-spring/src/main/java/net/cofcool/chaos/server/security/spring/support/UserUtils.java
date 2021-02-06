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

package net.cofcool.chaos.server.security.spring.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author CofCool
 */
public class UserUtils {

    public static Collection<? extends GrantedAuthority> convertUserRolesForSpring(Collection<? extends UserRole> userRoles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        userRoles.forEach(userRole -> authorities.add(new DelegatingGrantedAuthority(userRole)));

        return authorities;
    }

    static class DelegatingGrantedAuthority implements SpringAuthorityAdapter {

        private static final long serialVersionUID = -7544176970912040788L;

        private UserRole role;

        DelegatingGrantedAuthority(UserRole role) {
            this.role = role;
        }

        @Nullable
        @Override
        public UserRole parent() {
            return role.parent();
        }

        @Nullable
        @Override
        public Collection<UserRole> children() {
            return role.children();
        }

        @Override
        public String roleName() {
            return role.roleName();
        }

        @Override
        public int compareTo(@Nonnull UserRole userRole) {
            return role.compareTo(userRole);
        }

        @Override
        public boolean contains(@Nonnull String[] roles) {
            return role.contains(roles);
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof UserRole) {
                return role.roleName().equals(((UserRole) o).roleName());
            }

            return false;
        }

        @Override
        public int hashCode() {
            return role.roleName().hashCode();
        }
    }

}
