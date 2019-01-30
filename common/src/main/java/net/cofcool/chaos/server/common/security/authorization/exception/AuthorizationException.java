package net.cofcool.chaos.server.common.security.authorization.exception;

import net.cofcool.chaos.server.common.core.ServiceException;

/**
 * 授权相关异常
 *
 * @author CofCool
 */
public class AuthorizationException extends ServiceException {

    private static final long serialVersionUID = -3675915399264475516L;

    public AuthorizationException(String message) {
        super(message);
    }

}
