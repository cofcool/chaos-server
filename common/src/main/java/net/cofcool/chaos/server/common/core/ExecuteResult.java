package net.cofcool.chaos.server.common.core;

import java.util.function.Function;
import jdk.internal.jline.internal.Nullable;

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

    /**
     * 如果 {@link #successful()} 为 true，则执行 fn，否则返回 null
     *
     * @param fn 需要执行的 Function
     * @param <R> 返回数据类型
     * @return fn 执行结果
     */
    @Nullable
    default <R> R apply(Function<T, R> fn) {
        if (successful()) {
            return fn.apply(getEntity());
        }

        return null;
    }

}
