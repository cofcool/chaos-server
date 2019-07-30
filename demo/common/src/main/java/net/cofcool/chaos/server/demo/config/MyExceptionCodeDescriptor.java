package net.cofcool.chaos.server.demo.config;

import java.util.Map;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.core.support.SimpleExceptionCodeDescriptor;
import net.cofcool.chaos.server.demo.api.Constant;

public class MyExceptionCodeDescriptor extends SimpleExceptionCodeDescriptor {

    @Override
    protected Map<String, String> customize() {
        return Map.of(
            ExceptionCodeDescriptor.USER_PASSWORD_ERROR, Constant.PASSWORD_ERROR_VAL,
            ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC, Constant.PASSWORD_ERROR_DESC_VAL
        );
    }
}
