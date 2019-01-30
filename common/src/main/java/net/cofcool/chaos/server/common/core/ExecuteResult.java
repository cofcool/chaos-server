package net.cofcool.chaos.server.common.core;

/**
 * 执行结果
 *
 * @author CofCool
 */
public interface ExecuteResult<T> extends Result<T> {

    /**
     * 携带数据
     */
    T getEntity();

    /**
     * 影响数据数量，参考 {@link Result#EXECUTE_STATE_SUCCESSFUL}
     */
    int getState();

    /**
     * 描述信息
     */
    Message<T> getMessage();

    @Override
    default boolean successful() {
        return getState() >= EXECUTE_STATE_SUCCESSFUL;
    }

}
