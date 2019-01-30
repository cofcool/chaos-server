package net.cofcool.chaos.server.common.security.authorization;

import net.cofcool.chaos.server.common.security.User;

/**
 * 配置用户数据
 *
 * @author CofCool
 */
@FunctionalInterface
public interface UserProcessor {

    /**
     * 用户数据处理
     *
     * @param user 用户
     */
    void apply(User user);

}
