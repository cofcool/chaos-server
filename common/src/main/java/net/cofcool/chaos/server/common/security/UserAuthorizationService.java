package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.common.core.Message;

/**
 * 授权相关方法定义, 用户需实现该类处理相关逻辑
 * <br>
 * <b>注意</b> : 由于本实例创建较早, 会导致关联组件提早创建, 可能会导致关联组件的事务失效, 谨慎操作, 避免插入修改等操作。
 *
 * @author CofCool
 */
public interface UserAuthorizationService<T extends Auth, ID extends Serializable> {

    /**
     * 查询登陆用户
     * @param loginUser 登陆参数
     * @return 查询结果
     */
    User<T, ID> queryUser(AbstractLogin loginUser);

    /**
     * 检查用户, 登陆时调用
     * @param currentUser 当前用户
     * @return {@link Message}
     */
    Message<Boolean> checkUser(User<T, ID> currentUser);

    /**
     * 检查验证码
     * @param loginUser 请求数据
     * @return 是否通过验证
     */
    default boolean checkCaptcha(AbstractLogin loginUser) {
        return true;
    }

    /**
     * 配置登陆时的用户数据
     * @param currentUser 当前用户
     */
    void setupUserData(User<T, ID> currentUser);

    /**
     * 检查权限
     * @param servletRequest 请求
     * @param servletResponse 响应
     * @return 是否通过验证
     */
    boolean checkPermission(ServletRequest servletRequest, ServletResponse servletResponse);

    /**
     * 报告授权验证异常信息
     * <br>
     * <b>TODO</b>: 使用事件监听方式替代, 参考<code>org.springframework.security.authentication.event.AuthenticationSuccessEvent</code>等
     * @param authenticationInfo 授权信息
     * @param authenticationException 异常信息
     */
    default void reportAuthenticationExceptionInfo(Object authenticationInfo, Throwable authenticationException) {

    }

}
