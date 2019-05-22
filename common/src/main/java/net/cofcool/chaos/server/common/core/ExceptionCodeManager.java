package net.cofcool.chaos.server.common.core;

import java.util.Objects;

/**
 * 异常描述管理, 建议应用维护一个实例
 *
 * @author CofCool
 *
 * @see ExceptionCodeDescriptor
 */
public class ExceptionCodeManager {

    private final ExceptionCodeDescriptor descriptor;

    /**
     * 创建异常描述管理器
     * @param descriptor ExceptionCodeDescriptor 实例，不能为 {@literal null}
     */
    public ExceptionCodeManager(ExceptionCodeDescriptor descriptor) {
        Objects.requireNonNull(descriptor);
        this.descriptor = descriptor;
    }

    /**
     * 获取 ExceptionCodeDescriptor
     * @return ExceptionCodeDescriptor 实例
     */
    public ExceptionCodeDescriptor getDescriptor() {
        return descriptor;
    }

    public String getCode(String type) {
        return descriptor.code(type);
    }

    public String getDescription(String type) {
        return descriptor.description(type);
    }

}