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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * 用户角色
 *
 * @author CofCool
 */
public interface UserRole extends Comparable<UserRole>, Serializable {


    /**
     * 获取父角色
     *
     * @return UserRole
     */
    @Nullable
    UserRole parent();

    /**
     * 获取子角色
     *
     * @return Collection<UserRole>
     */
    @Nullable
    Collection<UserRole> children();

    /**
     * 角色名称
     * @return 角色名称
     */
    String roleName();

    /**
     * 使用 {@code roleName().hashCode()}
     * @return hashCode
     */
    int hashCode();

    /**
     * 比较 {@link #roleName()} 是否相同
     * @param obj obj
     * @return 是否相同
     */
    boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    default int compareTo(@Nonnull UserRole userRole) {
        return roleName().compareTo(userRole.roleName());
    }

    /**
     * 检测是否包含指定角色
     * @param roles 角色数组
     * @return 是否包含
     */
    default boolean contains(@Nonnull String[] roles) {
        boolean flag = roles.length == 0;

        int i = 0;
        while (i < roles.length && !flag) {
            if (roleName().equals(roles[i])) {
                flag = true;
            } else if (children() != null) {
                Iterator<UserRole> roleIterator = children().iterator();
                while (roleIterator.hasNext() && !flag) {
                    flag = roleIterator.next().contains(roles);
                }
            }

            i++;
        }

        return flag;
    }

}
