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
     * 扩展信息
     */
    Object getExt();

}
