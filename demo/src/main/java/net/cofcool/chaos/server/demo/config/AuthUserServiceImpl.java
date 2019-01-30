package net.cofcool.chaos.server.demo.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.authorization.AuthUserService;
import net.cofcool.chaos.server.core.support.UserConfiguration;
import net.cofcool.chaos.server.demo.item.UserData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author CofCool
 */
@Service
public class AuthUserServiceImpl implements AuthUserService, InitializingBean {

    private Map<String, User> userMap = new HashMap<>();

    @Override
    public User queryUser(AbstractLogin loginUser) {
        return userMap.get(loginUser.getUsername());
    }

    @Override
    public Message<Boolean> checkUser(User currentUser) {
        return Message.successful("", Boolean.TRUE);
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
        User user = UserConfiguration.buildDefaultUser();
        UserData userData = new UserData("1", "test data");
        user.setDetail(userData);
        userMap.put(user.getUserName(), user);
    }

    @Nullable
    @Override
    public User readCurrentUser() {
        return null;
    }
}
