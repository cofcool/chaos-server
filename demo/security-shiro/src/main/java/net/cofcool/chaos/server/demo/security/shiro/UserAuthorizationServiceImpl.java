package net.cofcool.chaos.server.demo.security.shiro;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.auto.config.ChaosProperties;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.demo.api.UserData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService<UserData, Long>,
    InitializingBean {

    @Resource
    private ChaosProperties chaosProperties;

    private Map<String, User<UserData, Long>> users = new HashMap<>();

    @Override
    public User<UserData, Long> queryUser(AbstractLogin loginUser) {
        return users.get(loginUser.getUsername());
    }

    @Override
    public Message<Boolean> checkUser(User<UserData, Long> currentUser) {
        return Message.of("", "", Boolean.TRUE);
    }

    @Override
    public void setupUserData(User<UserData, Long> currentUser) {

    }

    @Override
    public void checkPermission(ServletRequest servletRequest, ServletResponse servletResponse,
        Object authenticationInfo, String requestPath) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        User<UserData, Long> user = new User<>(
            chaosProperties.getAuth().getDefaultUsername(),
            chaosProperties.getAuth().getDefaultPassword()
        );
        user.setDetail(new UserData());
        user.setUserId(1L);
        user.addUserStatus(UserStatus.NORMAL);

        users.put(user.getUsername(), user);
    }
}
