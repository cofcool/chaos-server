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

import net.cofcool.chaos.server.common.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * 适配 {@link GrantedAuthority} 和 {@link UserRole}
 */
public interface SpringAuthorityAdapter extends GrantedAuthority, UserRole {

    @Override
    default String getAuthority() {
        return roleName();
    }
}
