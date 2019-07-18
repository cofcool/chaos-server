package net.cofcool.chaos.server.demo.item;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.demo.api.Login;
import net.cofcool.chaos.server.demo.api.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
        return Message.of("00", "OK", user);
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
}
