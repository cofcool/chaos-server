package net.cofcool.chaos.server.common.core;

import java.util.NoSuchElementException;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * 执行结果, 如果执行成功, 那 <code>entity</code> 必须有值
 *
 * @author CofCool
 */
public interface ExecuteResult<T> extends Result<T> {

    /**
     * 获取结果数据, 建议先调用 {@link #successful()} 确保执行成功
     *
     * @throws NoSuchElementException 执行未成功或数据不存在时抛出异常
     */
    T getEntity();

    /**
     * 执行状态, 参考 {@link net.cofcool.chaos.server.common.core.Result.ResultState}
     */
    ResultState getState();

    /**
     * 描述信息
     */
    Message<T> getMessage();

    @Override
    default boolean successful() {
        return getState() == ResultState.SUCCESSFUL;
    }

    /**
     * 如果 {@link #successful()} 为 true, 则执行 fn, 否则返回 null
     *
     * @param fn 需要执行的 Function
     * @param <R> 返回数据类型
     * @return fn 执行结果
     *
     * @throws NoSuchElementException 执行未成功或数据不存在时抛出异常
     */
    @Nullable
    default <R> R apply(Function<T, R> fn) {
        if (successful()) {
            return fn.apply(getEntity());
        }

        return null;
    }

}
