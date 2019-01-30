package net.cofcool.chaos.server.common.security;

/**
 * 密码处理，包括加密和解密
 *
 * @author CofCool
 */
public interface PasswordProcessor {

    /**
     * 加密
     * @param rawPassword 原密码
     * @return 加密后的密码
     */
    String process(String rawPassword);

    /**
     * 密码匹配
     * @param rawPassword 原密码
     * @param encodedPassword 用户真实密码
     * @return 是否匹配
     */
    boolean doMatch(String rawPassword, String encodedPassword);

}
