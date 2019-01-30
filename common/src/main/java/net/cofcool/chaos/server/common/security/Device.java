package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * 设备定义
 *
 * @author CofCool
 */
public interface Device extends Serializable {

    String ALLOW_ALL = "all";

    /**
     * 设备惟一标识
     */
    String getIdentifier();

    /**
     * 描述
     */
    String getDesc();

    /**
     * 是否需要安全检查
     */
    default Boolean shouldValidate() {
        return Boolean.FALSE;
    }

    /**
     * 是否包含
     */
    default boolean contained(@Nonnull String... identifiers) {
        boolean flag = identifiers.length == 0 || ALLOW_ALL.equals(identifiers[0]);

        int i = 0;
        while (i < identifiers.length && !flag) {
            flag = getIdentifier().equals(identifiers[i]);
            i++;
        }

        return flag;
    }

}
