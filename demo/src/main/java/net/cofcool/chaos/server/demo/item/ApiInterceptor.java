package net.cofcool.chaos.server.demo.item;

import javax.annotation.Resource;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.core.aop.ApiProcessingInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author CofCool
 */
@Component
public class ApiInterceptor extends ApiProcessingInterceptor {

    @Resource
    private AuthService authService;


    @Override
    protected User getUser() {
        return authService.readCurrentUser();
    }

}
