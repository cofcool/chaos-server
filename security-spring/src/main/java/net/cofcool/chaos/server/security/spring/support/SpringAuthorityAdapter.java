package net.cofcool.chaos.server.security.spring.support;

import net.cofcool.chaos.server.common.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * 适配 {@link GrantedAuthority} 和 {@link UserRole}
 */
public interface SpringAuthorityAdapter extends GrantedAuthority, UserRole {

    @Override
    default String getAuthority() {
        return roleName();
    }
}
