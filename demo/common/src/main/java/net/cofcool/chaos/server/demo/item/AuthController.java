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

package net.cofcool.chaos.server.demo.item;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.SimplePage;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.demo.api.Login;
import net.cofcool.chaos.server.demo.api.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CofCool
 */
@Api
@RestController
@RequestMapping(value = "/auth", method = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    private AuthService<UserData, Long> authService;

    private ExceptionCodeManager exceptionCodeManager;

    private PersonService<Person> personService;

    @Autowired
    public void setPersonService(PersonService<Person> personService) {
        this.personService = personService;
    }

    @Autowired
    public void setExceptionCodeManager(
        ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    @Autowired
    public void setAuthService(
        AuthService<UserData, Long> authService) {
        this.authService = authService;
    }

    @RequestMapping("/login")
    public Message<User<UserData, Long>> home(@RequestBody Login login, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(request, response, login);
    }

    @RequestMapping("/user")
    public Message<User> test(User user) {
        return Message.of(
            exceptionCodeManager.getCode(ExceptionCodeDescriptor.SERVER_OK),
            exceptionCodeManager.getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC),
            user);
    }

    @RequestMapping("/unauth")
    public Message unauth(String ex) {
        return Message.of(
            exceptionCodeManager.getCode(ExceptionCodeDescriptor.AUTH_ERROR),
            ex
        );
    }


    @RequestMapping("/unlogin")
    public Message unlogin(String ex) {
        return Message.of(
            exceptionCodeManager.getCode(ExceptionCodeDescriptor.NO_LOGIN),
            ex
        );
    }

    @RequestMapping("/query")
    public Message query(@RequestBody SimplePage<Person> page) {
        return personService.query(page, page.getCondition()).result();
    }
}
