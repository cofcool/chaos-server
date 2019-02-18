package net.cofcool.chaos.server.common.security.authorization;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.User;

/**
 * @author CofCool
 */
public interface AuthUserService<T extends Auth<D, ID>, D extends Serializable, ID extends Serializable> extends AuthService<T, D, ID> {

    /**
     * 查询登陆用户
     * @param loginUser 登陆参数
     * @return 查询结果
     */
    User<T, D, ID> queryUser(AbstractLogin loginUser);

    /**
     * 检查用户，登陆时调用
     * @param currentUser 当前用户
     * @return {@link Message}
     */
    Message<Boolean> checkUser(User<T, D, ID> currentUser);

    /**
     * 配置登陆时的用户数据
     * @param currentUser 当前用户
     */
    void setupUserData(User<T, D, ID> currentUser);

    /**
     * 注册
     * @param registerUser 用户相关参数
     * @return 执行结果 {@link ExecuteResult}
     */
    default ExecuteResult register(AbstractLogin registerUser) {
        throw new UnsupportedOperationException();
    }

    /**
     * 重置密码
     * @param loginUser 用户相关参数
     * @return 执行结果 {@link ExecuteResult}
     */
    default ExecuteResult resetPassword(AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    /**
     * 忘记密码
     * @param loginUser 用户相关参数
     * @return 执行结果 {@link ExecuteResult}
     */
    default ExecuteResult forgetPassword(AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    /**
     * 配置用户数据处理器
     *
     * @param userProcessor 用户数据处理器
     */
    default void setUserProcessor(UserProcessor userProcessor) {

    }

    /**
     * 报告授权验证异常信息
     * @param authenticationInfo 授权信息
     * @param authenticationException 异常信息
     * @throws RuntimeException 处理时可抛出相关异常
     */
    default void reportAuthenticationExceptionInfo(Object authenticationInfo, Exception authenticationException) throws RuntimeException {

    }

    @Nullable
    @Override
    default User<T, D, ID> readCurrentUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Message<User<T, D, ID>> login(AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        throw new UnsupportedOperationException();
    }

}
