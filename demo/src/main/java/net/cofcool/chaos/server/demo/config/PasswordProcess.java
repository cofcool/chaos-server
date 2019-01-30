package net.cofcool.chaos.server.demo.config;

import net.cofcool.chaos.server.common.security.PasswordProcessor;
import org.springframework.stereotype.Component;

/**
 * @author CofCool
 */
@Component
public class PasswordProcess implements PasswordProcessor {

    @Override
    public String process(String rawPassword) {
        return null;
    }

    @Override
    public boolean doMatch(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
