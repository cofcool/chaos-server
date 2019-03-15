package net.cofcool.chaos.server.common.util;

import javax.annotation.Nullable;

/**
 * String 工具类
 *
 * @author CofCool
 */
public final class StringUtils {

    public static final String EMPTY_STRING = "";


    /**
     * 判断字符串是否为空
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 去除头尾空格, 如果去除后为空则返回null
     */
    public static String clean(String in) {
        String out = in;

        if (in != null) {
            out = in.trim();
            if (out.equals(EMPTY_STRING)) {
                out = null;
            }
        }

        return out;
    }

    /**
     * 获取字符长度
     */
    public static int length(@Nullable CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

}
