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

package net.cofcool.chaos.server.security.shiro.access;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AbstractAuthenticationStrategy;
import org.apache.shiro.realm.Realm;

/**
 * 继承 {@link AbstractAuthenticationStrategy}, 重写 {@link #afterAttempt(Realm, AuthenticationToken, AuthenticationInfo, AuthenticationInfo, Throwable)} 方法, 当应用抛出异常时, shiro 应直接抛出该异常
 *
 * @author CofCool
 */
public class ExceptionAuthenticationStrategy extends AbstractAuthenticationStrategy {


    @Override
    public AuthenticationInfo afterAllAttempts(AuthenticationToken token,
        AuthenticationInfo aggregate) throws AuthenticationException {
        return super.afterAllAttempts(token, aggregate);
    }

    @Override
    public AuthenticationInfo afterAttempt(Realm realm, AuthenticationToken token,
        AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t)
        throws AuthenticationException {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        return super.afterAttempt(realm, token, singleRealmInfo, aggregateInfo, t);
    }
}
