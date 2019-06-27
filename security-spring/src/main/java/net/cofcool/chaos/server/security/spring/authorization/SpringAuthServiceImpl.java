package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * Spring Security 授权管理相关处理, 不支持 {@link #login(AbstractLogin)}, 可通过
 * {@link SpringUserAuthorizationService} 实现
 *
 * @author CofCool
 *
 * @see AuthService
 * @see UserDetailsService
 * @see UserDetailsManager
 */
@Slf4j
public class SpringAuthServiceImpl<T extends Auth, ID extends Serializable> implements AuthService<T, ID> {


    @Override
    public Message<User<T, ID>> login(AbstractLogin loginUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        new SecurityContextLogoutHandler()
                .logout(httpServletRequest, httpServletResponse, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public User<T, ID> readCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (authentication.isAuthenticated() && principal instanceof User) {
                return (User<T, ID>) principal;
            }
        }

        return null;
    }

}
