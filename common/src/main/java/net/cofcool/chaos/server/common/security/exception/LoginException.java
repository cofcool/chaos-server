package net.cofcool.chaos.server.common.security.exception;

import net.cofcool.chaos.server.common.core.ExceptionLevel;

public class LoginException extends AuthorizationException {

    private static final long serialVersionUID = 1334911102389930098L;

    public LoginException(String message, String code) {
        super(message, code);
    }

    @Override
    public int getLevel() {
        return ExceptionLevel.LOWEST_LEVEL;
    }
}

