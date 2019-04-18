package net.cofcool.chaos.server.common.core;

/**
 * 异常描述管理
 *
 * @author CofCool
 *
 * @see ExceptionCodeDescriptor
 */
public class ExceptionCodeManager {

    private final ExceptionCodeDescriptor descriptor;

    public ExceptionCodeManager(ExceptionCodeDescriptor descriptor) {
        if (descriptor == null) {
            this.descriptor = SimpleExceptionCodeDescriptor.DEFAULT_DESCRIPTOR;
        } else {
            this.descriptor = descriptor;
        }
    }

    /**
     * 获取 ExceptionCodeDescriptor
     * @return ExceptionCodeDescriptor 实例
     */
    public ExceptionCodeDescriptor getDescriptor() {
        return descriptor;
    }

    public String getCode(String type) {
        return descriptor.getCode(type);
    }

    public String getDescription(String type) {
        return descriptor.getDescription(type);
    }

}