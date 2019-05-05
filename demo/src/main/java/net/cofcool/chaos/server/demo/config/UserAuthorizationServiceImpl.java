package net.cofcool.chaos.server.demo.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.UserRole;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.demo.item.UserData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author CofCool
 */
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService, InitializingBean {

    private Map<String, User> userMap = new HashMap<>();

    @Override
    public User queryUser(AbstractLogin loginUser) {
        return userMap.get(loginUser.getUsername());
    }

    @Override
    public Message<Boolean> checkUser(User currentUser) {
        return Message.of("", "", Boolean.TRUE);
    }

    @Override
    public void setupUserData(User currentUser) {

    }

    @Override
    public boolean checkPermission(ServletRequest servletRequest, ServletResponse servletResponse) {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        User user = buildDefaultUser();
        userMap.put(user.getUsername(), user);
    }

    private User<? extends Auth, Long> buildDefaultUser() {
        User<UserData, Long>defaultUser = new User<>();
        defaultUser.addRole(new UserRole() {
            private static final long serialVersionUID = 5571835917896222870L;

            @Nullable
            @Override
            public UserRole getParent() {
                return null;
            }

            @Nullable
            @Override
            public Collection<UserRole> getChildren() {
                return null;
            }

            @Override
            public int getRoleId() {
                return 0;
            }

        });
        defaultUser.setUsername("root");
        defaultUser.setPassword("000000");
        defaultUser.setUserId(0L);
        defaultUser.setRegisterTime(new Date(0));
        defaultUser.setLatestLoginTime(new Date());
        defaultUser.setDetail(new UserData());
        defaultUser.addUserStatus(UserStatus.NORMAL);

        return defaultUser;
    }

}
