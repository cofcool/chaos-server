package net.cofcool.chaos.server.demo.item;

import javax.annotation.Resource;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.authorization.AuthService;
import net.cofcool.chaos.server.core.aop.AbstractApiInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author CofCool
 */
@Component
public class ApiInterceptor extends AbstractApiInterceptor {

    @Resource
    private AuthService authService;


    @Override
    protected User getUser() {
        return authService.readCurrentUser();
    }

}
