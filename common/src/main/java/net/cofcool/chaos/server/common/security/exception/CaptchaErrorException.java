package net.cofcool.chaos.server.common.security.exception;

/**
 * 验证码错误时抛出
 */
public class CaptchaErrorException extends AuthorizationException {

    private static final long serialVersionUID = -2198452931188019140L;

    public CaptchaErrorException(String message, String code) {
        super(message, code);
    }

    @Override
    public int getLevel() {
        return LOWEST_LEVEL;
    }
}
