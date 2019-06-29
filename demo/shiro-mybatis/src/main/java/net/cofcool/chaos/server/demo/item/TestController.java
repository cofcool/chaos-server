package net.cofcool.chaos.server.demo.item;

import javax.annotation.Resource;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.demo.api.UserData;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CofCool
 */
@Api
@RestController
@RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
public class TestController {

    @Resource
    private AuthService authService;

    @Resource
    private PersonService personService;

    @RequestMapping("/")
    public String home() {
        return "hello";
    }

    @RequestMapping("/test")
    public User test(@RequestBody UserData data, User user) {
        return user;
    }

    @RequestMapping("/query")
    public Message query(@RequestBody Page<Person> condition) {
        return personService.query(condition, condition.getCondition()).result();
    }

}
