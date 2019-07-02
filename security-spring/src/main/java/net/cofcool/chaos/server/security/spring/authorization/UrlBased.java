package net.cofcool.chaos.server.security.spring.authorization;

import java.util.Collection;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.AuthorizationException;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.Assert;

/**
 * 权限处理, 通过 {@link UserAuthorizationService#checkPermission(ServletRequest, ServletResponse, Object, String)} 实现
 *
 * @see org.springframework.security.access.AccessDecisionManager
 */
@Slf4j
public class UrlBased implements AccessDecisionManager {

    private final UserAuthorizationService userAuthorizationService;

    public UrlBased(
        UserAuthorizationService userAuthorizationService) {
        Assert.notNull(userAuthorizationService, "userAuthorizationService is required");
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    public void decide(Authentication authentication, Object object,
        Collection<ConfigAttribute> configAttributes)
        throws AccessDeniedException, InsufficientAuthenticationException {
        FilterInvocation invocation = (FilterInvocation) object;

        log.debug("decide {}'s request access: {}", authentication.getPrincipal().toString(), invocation.getRequestUrl());

        try {
            userAuthorizationService
                .checkPermission(invocation.getRequest(), invocation.getResponse(), authentication,
                    invocation.getRequestUrl());
        } catch (AccessDeniedException e) {
            userAuthorizationService.reportAuthenticationExceptionInfo(authentication, e);
            throw e;
        } catch (AuthorizationException e) {
            userAuthorizationService.reportAuthenticationExceptionInfo(authentication, e);

            throw new AccessDeniedException("Access is denied", e);
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
