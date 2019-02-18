package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthConstant;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.common.security.authorization.AuthUserService;
import net.cofcool.chaos.server.common.util.WebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.Assert;

/**
 * @author CofCool
 */
@Slf4j
public class SpringAuthServiceImpl<T extends Auth<D, ID>, D extends Serializable, ID extends Serializable> implements AuthService<T, D, ID>, UserDetailsManager,
    InitializingBean {

    private AuthUserService<T, D, ID> authUserService;

    public AuthUserService<T, D, ID> getAuthUserService() {
        return authUserService;
    }

    @SuppressWarnings("unchecked")
    public void setAuthUserService(AuthUserService authUserService) {
        this.authUserService = authUserService;
        this.authUserService.setUserProcessor(this::storageUser);
    }

    protected void storageUser(User currentUser) {
        WebUtils.getRequest().getSession().setAttribute(AuthConstant.LOGINED_USER_KEY, currentUser);
    }

    @Override
    public Message<User<T, D, ID>> login(AbstractLogin loginUser) {
        return Message.successful( "Ok", authUserService.queryUser(loginUser));
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        new SecurityContextLogoutHandler()
                .logout(httpServletRequest, httpServletResponse, null);
    }

    @Override
    public boolean checkPermission(ServletRequest servletRequest, ServletResponse servletResponse) {
        return authUserService.checkPermission(servletRequest, servletResponse);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public User<T, D, ID> readCurrentUser() {
        return (User<T, D, ID>) WebUtils.getRequest().getSession().getAttribute(AuthConstant.LOGINED_USER_KEY);
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
        return authUserService.queryUser(new DefaultLogin(username, "")) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User<T, D, ID> user = authUserService.queryUser(new DefaultLogin(username, ""));

        if (user == null) {
            UsernameNotFoundException e = new UsernameNotFoundException(username);
            authUserService.reportAuthenticationExceptionInfo(username, e);
            throw e;
        }

        return UserDetail.of(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getAuthUserService(), "authUserService - this argument is required; it must not be null");
    }
}
