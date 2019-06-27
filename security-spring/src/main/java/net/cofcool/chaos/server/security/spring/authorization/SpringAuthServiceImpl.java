package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.Assert;

/**
 * @author CofCool
 */
@Slf4j
public class SpringAuthServiceImpl<T extends Auth, ID extends Serializable> implements AuthService<T, ID>, UserDetailsManager, InitializingBean {

    private UserAuthorizationService<T, ID> userAuthorizationService;

    private ExceptionCodeManager exceptionCodeManager;

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    public void setExceptionCodeManager(
        ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    public UserAuthorizationService<T, ID> getUserAuthorizationService() {
        return userAuthorizationService;
    }

    @SuppressWarnings("unchecked")
    public void setUserAuthorizationService(UserAuthorizationService authUserService) {
        this.userAuthorizationService = authUserService;
        // note: 在 Spring Security 中如何配置
        this.userAuthorizationService.setUserProcessor(null);
    }

    @Override
    public Message<User<T, ID>> login(AbstractLogin loginUser) {
        return Message.of(
            exceptionCodeManager.getCode(ExceptionCodeDescriptor.SERVER_OK),
            exceptionCodeManager.getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC),
            userAuthorizationService.queryUser(loginUser)
        );
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

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userExists(String username) {
        return userAuthorizationService.queryUser(new DefaultLogin(username, "")) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User<T, ID> user = userAuthorizationService.queryUser(new DefaultLogin(username, ""));

        if (user == null) {
            UsernameNotFoundException e = new UsernameNotFoundException(username);
            userAuthorizationService.reportAuthenticationExceptionInfo(username, e);
            throw e;
        }

        return UserDetail.of(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getUserAuthorizationService(), "userAuthorizationService - this argument is required; it must not be null");
    }
}
