package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.LoginException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * 为了适配 {@link UserDetailsManager}  和 {@link UserAuthorizationService}, 默认实现 {@link #loadUserByUsername(String)}
 *
 * @param <T> 用户详细数据
 * @param <ID> 用户 ID
 *
 * @author CofCool
 */
public interface SpringUserAuthorizationService<T extends Auth, ID extends Serializable> extends
    UserAuthorizationService<T, ID>, UserDetailsManager {

    /**
     * {@inheritDoc}
     *
     * @throws LoginException 如果 {@link #checkUser(User)} 返回 <code>false</code>
     */
    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(new DefaultLogin(username, ""));
    }

    /**
     * 当 <code>authRequest</code> 为 {@link JsonAuthenticationToken} 时调用
     * @param login {@link AbstractLogin} 实例
     * @return UserDetails
     * @throws UsernameNotFoundException 当用户不存在时
     * @throws LoginException 如果 {@link #checkUser(User)} 返回 <code>false</code>
     */
    default UserDetails loadUserByUsername(AbstractLogin login) throws UsernameNotFoundException {
        User<T, ID> user = queryUser(login);

        if (user == null) {
            UsernameNotFoundException e = new UsernameNotFoundException(login.getUsername());
            reportAuthenticationExceptionInfo(login, e);

            throw e;
        }

        Message<Boolean> userState = checkUser(user);
        if (!userState.data()) {
            LoginException exception = new LoginException(userState.message(), userState.code());
            reportAuthenticationExceptionInfo(login, exception);

            throw exception;
        }

        return UserDetail.of(user);
    }

}
