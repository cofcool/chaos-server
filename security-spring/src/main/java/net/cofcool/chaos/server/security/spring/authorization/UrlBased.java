package net.cofcool.chaos.server.security.spring.authorization;

import java.util.Collection;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.common.security.UserAuthorizationService;
import net.cofcool.chaos.server.common.security.exception.AuthorizationException;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.Assert;

/**
 * 权限处理, 通过 {@link UserAuthorizationService#checkPermission(ServletRequest, ServletResponse, Object, String)} 实现
 *
 * @see org.springframework.security.access.AccessDecisionManager
 */
@Slf4j
public class UrlBased extends UnanimousBased {


    public UrlBased(
        List<AccessDecisionVoter<? extends Object>> decisionVoters,
        UserAuthorizationService userAuthorizationService,
        boolean usingUserAuthorizationService) {
        super(decisionVoters);

        Assert.notNull(userAuthorizationService, "userAuthorizationService is required");
        getDecisionVoters().add(new PermissionVoter<>(userAuthorizationService, usingUserAuthorizationService));
    }

    class PermissionVoter<S> implements AccessDecisionVoter<S> {

        private final UserAuthorizationService userAuthorizationService;

        private final boolean usingUserAuthorizationService;

        PermissionVoter(
            UserAuthorizationService userAuthorizationService,
            boolean usingUserAuthorizationService) {
            this.userAuthorizationService = userAuthorizationService;
            this.usingUserAuthorizationService = usingUserAuthorizationService;
        }

        @Override
        public boolean supports(ConfigAttribute attribute) {
            return usingUserAuthorizationService;
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public int vote(Authentication authentication, S object,
            Collection<ConfigAttribute> attributes) {
            if (authentication == null) {
                return ACCESS_DENIED;
            }

            FilterInvocation invocation = (FilterInvocation) object;

            log.debug("vote {}'s request access: {}", authentication.getPrincipal().toString(), invocation.getRequestUrl());

            try {
                userAuthorizationService
                    .checkPermission(invocation.getRequest(), invocation.getResponse(), authentication,
                        invocation.getRequestUrl());

                return ACCESS_GRANTED;
            } catch (AccessDeniedException | AuthorizationException e) {
                log.debug("vote error when invoke the userAuthorizationService.checkPermission", e);
            }

            return ACCESS_DENIED;
        }
    }
}
