package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
     * 角色代码
     *
     * @return 角色代码
     */
    int roleId();

    /**
     * 角色名称
     * @return 角色名称
     */
    default String roleName() {
        return String.valueOf(roleId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int compareTo(@Nonnull UserRole userRole) {
        return Integer.compare(roleId(), userRole.roleId());
    }

    /**
     * 检测是否包含指定角色
     * @param roles 角色ID数组
     * @return 是否包含
     */
    default boolean contains(@Nonnull int[] roles) {
        boolean flag = roles.length == 0;

        int i = 0;
        while (i < roles.length && !flag) {
            if (roles[i] == roleId()) {
                flag = true;
            } else if (children()!= null){
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
