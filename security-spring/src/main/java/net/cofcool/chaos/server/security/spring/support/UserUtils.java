package net.cofcool.chaos.server.security.spring.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.cofcool.chaos.server.common.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author CofCool
 */
public class UserUtils {

    public static Collection<? extends GrantedAuthority> convertUserRolesForSpring(Collection<? extends UserRole> userRoles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        userRoles.forEach(userRole -> authorities.add(new GrantedAuthority() {
            private static final long serialVersionUID = -8863145628136361885L;

            @Override
            public String getAuthority() {
                return userRole.roleName();
            }
        }));

        return authorities;
    }

}
