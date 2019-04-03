package net.cofcool.chaos.server.common.core;

/**
 * 查询结果
 *
 * @param <T> 列表数据类型
 * @param <R> 结果数据类型
 *
 * @author CofCool
 */
public interface QueryResult<T, R> extends Result<R> {

    /**
     * 分页数据
     * @return {@link Page}
     */
    Page<T> getPage();

    /**
     * QueryResult 携带的额外数据
     *
     * @return 扩展数据
     */
    Object getExt();

    /**
     * 创建 QueryResult 实例的方便方法
     *
     * @param page 分页数据
     * @param <T> 数据类型
     * @return QueryResult 实例
     */
    static <T> QueryResult<T, ?> of(Page<T> page) {
        return new SimpleQueryResult<>(page);
    }

    /**
     * 创建 QueryResult 实例的方便方法
     *
     * @param page 分页数据
     * @param ext 扩展数据
     * @param <T>  数据类型
     * @return QueryResult 实例
     */
    static <T> QueryResult<T, ?> of(Page<T> page, Object ext) {
        return new SimpleQueryResult<>(page, ext);
    }

}
