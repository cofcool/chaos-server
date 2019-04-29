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
    Message<User<T, ID>> login(AbstractLogin loginUser);

    /**
     * 退出登陆
     */
    void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 读取当前登陆用户数据
     * @return {@link User}
     */
    @Nullable
    User<T, ID> readCurrentUser();

}
