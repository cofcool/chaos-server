package net.cofcool.chaos.server.common.core;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * 执行结果, 如果 {@link #successful()} 为 "true", 那 <code>entity</code> 必须有值
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


    @Override
    default boolean successful() {
        return getState() == ResultState.SUCCESSFUL;
    }

    /**
     * 获取结果数据, 如果为"NULL", 则返回"newVal"
     * @param newVal 替代数据
     * @return 结果数据
     */
    T orElse(T newVal);

    /**
     * 如果 <b>Entity</b> 不为"NULL", 则执行"fn", 否则返回"NULL"
     *
     * @param fn 需要执行的 Function
     * @param <R> 返回数据类型
     * @return fn 执行结果
     */
    @Nullable
    default <R> R apply(Function<T, R> fn) {
        if (orElse(null) != null) {
            return fn.apply(getEntity());
        }

        return null;
    }

    /**
     * 创建 ExecuteResult 实例的方便方法
     *
     * @param entity 结果数据
     * @param state 执行状态
     * @param code 描述码
     * @param msg 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     *
     * @see SimpleExecuteResult
     */
    static <T> ExecuteResult<T> of(T entity, ResultState state, String code, String msg) {
        return new SimpleExecuteResult<>(state, Message.of(code, msg, entity));
    }

    /**
     * 创建 ExecuteResult 实例的方便方法
     *
     * @param state 执行状态
     * @param msg 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     *
     * @see SimpleExecuteResult
     */
    static <T> ExecuteResult<T> of(ResultState state, Message<T> msg) {
        return new SimpleExecuteResult<>(state, msg);
    }

}
