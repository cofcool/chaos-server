package net.cofcool.chaos.server.common.security.authorization.exception;

public class CaptchaException extends AuthorizationException {

    private static final long serialVersionUID = -2198452931188019140L;

    public CaptchaException(String message, String code) {
        super(message, code);
    }

    @Override
    public int getLevel() {
        return LOWEST_LEVEL;
    }
}
