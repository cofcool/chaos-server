package net.cofcool.chaos.server.security.spring.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author CofCool
 */
public class UserUtils {

    public static Collection<? extends GrantedAuthority> convertUserRolesForSpring(Collection<? extends UserRole> userRoles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        userRoles.forEach(userRole -> authorities.add(new DelegatingGrantedAuthority(userRole)));

        return authorities;
    }

    static class DelegatingGrantedAuthority implements SpringAuthorityAdapter {

        private static final long serialVersionUID = -7544176970912040788L;

        private UserRole role;

        DelegatingGrantedAuthority(UserRole role) {
            this.role = role;
        }

        @Nullable
        @Override
        public UserRole parent() {
            return role.parent();
        }

        @Nullable
        @Override
        public Collection<UserRole> children() {
            return role.children();
        }

        @Override
        public int roleId() {
            return role.roleId();
        }

        @Override
        public String roleName() {
            return role.roleName();
        }

        @Override
        public int compareTo(@Nonnull UserRole userRole) {
            return role.compareTo(userRole);
        }

        @Override
        public boolean contains(@Nonnull int[] roles) {
            return role.contains(roles);
        }
    }

}
