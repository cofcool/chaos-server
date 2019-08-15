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

package net.cofcool.chaos.server.demo.security.spring;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.auto.config.ChaosProperties;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserStatus;
import net.cofcool.chaos.server.demo.api.UserData;
import net.cofcool.chaos.server.security.spring.authorization.SpringUserAuthorizationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationServiceImpl implements SpringUserAuthorizationService<UserData, Long>,
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
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        User<UserData, Long> user = new User<>(
            chaosProperties.getAuth().getDefaultUsername(),
            chaosProperties.getAuth().getDefaultPassword()
        );
        user.setUserId(1L);
        user.setDetail(new UserData(user.getUsername(), user.getUserId()));
        user.addUserStatus(UserStatus.NORMAL);

        users.put(user.getUsername(), user);
    }
}
