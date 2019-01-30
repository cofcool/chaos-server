package net.cofcool.chaos.server.common.security.authorization.exception;

import net.cofcool.chaos.server.common.core.ExceptionLevel;

public class UserNotExistException extends AuthorizationException {

    private static final long serialVersionUID = -1672933472827014235L;

    public UserNotExistException(String message) {
        super(message);
    }

    @Override
    public int getLevel() {
        return ExceptionLevel.LOWEST_LEVEL;
    }
}
