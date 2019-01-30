package net.cofcool.chaos.server.core.support;

import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserRole;
import net.cofcool.chaos.server.common.security.UserStatus;

/**
 * @author CofCool
 */
public class UserConfiguration {

    /**
     * 默认用户
     */
    public static User<? extends Auth, String, Long> buildDefaultUser() {
        User<DefaultUserData, String, Long>defaultUser = new User<>();
        defaultUser.addRole(new UserRole () {
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
        defaultUser.setUserName(RootManager.username());
        defaultUser.setLoginPwd(RootManager.password());
        defaultUser.setNickName("Root");
        defaultUser.setUserId(0L);
        defaultUser.setRegisterTime(new Date(0));
        defaultUser.setLatestLoginTime(new Date());
        defaultUser.setDetail(new DefaultUserData());
        defaultUser.addUserStatus(UserStatus.NORMAL);

        return defaultUser;
    }

    static class DefaultUserData implements Auth<String, Long> {

        private static final long serialVersionUID = 5680703572386595237L;

        @Override
        public Long getId() {
            return 1L;
        }

        @Override
        public String getData() {
            return "";
        }
    }

}
