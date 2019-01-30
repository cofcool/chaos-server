package net.cofcool.chaos.server.security.spring.authorization;

import java.io.Serializable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author CofCool
 */
public class JsonAuthenticationToken extends UsernamePasswordAuthenticationToken implements Serializable {

    private static final long serialVersionUID = 5795425390785540829L;

    public JsonAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
