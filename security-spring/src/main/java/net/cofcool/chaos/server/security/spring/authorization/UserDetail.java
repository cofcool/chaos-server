package net.cofcool.chaos.server.security.spring.authorization;

import java.util.Collection;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.security.spring.support.UserUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户数据
 *
 * @author CofCool
 */
public class UserDetail extends User implements
    UserDetails {

    private static final long serialVersionUID = -634235060622704818L;

    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserUtils.convertUserRolesForSpring(getRoles());
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getUserStatuses().contains(UserStatus.LOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return getUserStatuses().contains(UserStatus.NORMAL);
    }

    /**
     * 根据 User 创建 UserDetail 实例
     * @param user user 实例
     * @return UserDetail
     */
    public static UserDetail of(User user) {
        UserDetail userDetail = new UserDetail();
        user.cloneUser(userDetail);
        userDetail.setAccountNonExpired(true);
        userDetail.setCredentialsNonExpired(true);

        return userDetail;
    }
}
