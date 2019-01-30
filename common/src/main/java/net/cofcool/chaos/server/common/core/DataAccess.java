package net.cofcool.chaos.server.common.core;

import java.util.List;

/**
 * 常用 <b>DAO</b> 方法
 *
 * @author CofCool
 */
public interface DataAccess<T> {

    /**
     * 添加
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> add(T entity);

    /**
     * 删除
     *
     * @param entity 实体
     *
     * @return 影响数据数目
     */
    Integer delete(T entity);

    /**
     * 修改
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> update(T entity);

    /**
     * 分页查询
     *
     * @param condition 条件
     * @param entity 实体
     *
     * @return {@link QueryResult} 对象
     */
    QueryResult <T, ?> query(Page<T> condition, T entity);

    /**
     * 查询所有
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<List<T>> queryAll(T entity);

    /**
     * 查询详情
     *
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    ExecuteResult<T> queryById(T entity);

}

