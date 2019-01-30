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
    UserRole getParent();

    /**
     * 获取子角色
     *
     * @return Collection<UserRole>
     */
    @Nullable
    Collection<UserRole> getChildren();

    /**
     * 角色代码
     *
     * @return 角色代码
     */
    int getRoleId();

    /**
     * 角色名称
     * @return 角色名称
     */
    default String getName() {
        return String.valueOf(getRoleId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default int compareTo(@Nonnull UserRole userRole) {
        return getRoleId() - userRole.getRoleId();
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
            if (roles[i] == getRoleId()) {
                flag = true;
            } else if (getChildren()!= null){
                Iterator<UserRole> roleIterator = getChildren().iterator();
                while (roleIterator.hasNext() && !flag) {
                    flag = roleIterator.next().contains(roles);
                }
            }

            i++;
        }

        return flag;
    }

}
