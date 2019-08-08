/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.security.spring.authorization;

import net.cofcool.chaos.server.common.security.PasswordProcessor;
import net.cofcool.chaos.server.common.security.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * 实现 {@link org.springframework.security.authentication.AuthenticationProvider}, 处理 {@link UsernamePasswordAuthenticationToken}, 通过调用 {@link SpringUserAuthorizationService} 实现
 * 
 * @see DaoAuthenticationProvider
 */
public class SpringDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * The plaintext password used to perform
     * PasswordEncoder#matches(CharSequence, String)}  on when the user is
     * not found to avoid SEC-2056.
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    /**
     * The password used to perform
     * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
     * not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private volatile String userNotFoundEncodedPassword;

    private PasswordProcessor passwordProcessor;

    private SpringUserAuthorizationService userAuthorizationService;

    public PasswordProcessor getPasswordProcessor() {
        return passwordProcessor;
    }

    public void setPasswordProcessor(PasswordProcessor passwordProcessor) {
        this.passwordProcessor = passwordProcessor;
    }

    public SpringUserAuthorizationService getUserAuthorizationService() {
        return userAuthorizationService;
    }

    public void setUserAuthorizationService(SpringUserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!getPasswordProcessor().doMatch(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
        }

        getUserAuthorizationService().setupUserData((User) userDetails);
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Assert.notNull(this.userAuthorizationService, "A userAuthorizationService must be set");
    }

    protected final UserDetails retrieveUser(String username,
        UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        prepareTimingAttackProtection();
        try {
            SpringUserAuthorizationService service = getUserAuthorizationService();
            UserDetails loadedUser;
            if (authentication instanceof JsonAuthenticationToken) {
                loadedUser = service
                    .loadUserByUsername(((JsonAuthenticationToken)authentication).getLogin());
            } else {
                loadedUser = service.loadUserByUsername(username);
            }

            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        }
        catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal,
        Authentication authentication, UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = passwordProcessor.process(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordProcessor.doMatch(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

}
