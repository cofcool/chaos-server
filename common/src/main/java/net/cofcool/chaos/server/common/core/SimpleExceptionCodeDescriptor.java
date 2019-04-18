package net.cofcool.chaos.server.common.core;

/**
 * ExceptionCodeDescriptor 的默认实现, 处理默认信息, 包括 {@link ExceptionCodeDescriptor#SERVER_ERR} 等,
 *  应用可使用 {@link ResourceExceptionCodeDescriptor}, 该类可读取"Spring"配置的"message"信息
 * @author CofCool
 */
public class SimpleExceptionCodeDescriptor implements ExceptionCodeDescriptor {

    /**
     * 获取默认的 ExceptionCodeDescriptor
     */
    protected static final ExceptionCodeDescriptor DEFAULT_DESCRIPTOR = new SimpleExceptionCodeDescriptor();

    @Override
    public String getCode(String type) {
        return type;
    }

    @Override
    public String getDescription(String type) {
        return type;
    }

}
