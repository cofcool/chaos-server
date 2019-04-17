package net.cofcool.chaos.server.common.core;


import java.util.Objects;

/**
 * 基础 <b>Service</b>, 简化查询操作
 *
 * @see DataAccess
 *
 * @author CofCool
 */
public abstract class SimpleService<T> implements DataAccess<T> {

    private PageProcessor<T> pageProcessor;

    public PageProcessor<T> getPageProcessor() {
        return pageProcessor;
    }

    public void setPageProcessor(PageProcessor<T> pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    @Override
    public QueryResult<T, ?> query(Page<T> condition, T entity) {
        Page<T> page = PageSupport.checkPage(condition);

        Objects.requireNonNull(getPageProcessor());
        return QueryResult.of(getPageProcessor().process(page, queryWithPage(page, entity)), "", "");
    }

    /**
     * 分页查询
     *
     * @param condition 条件
     * @param entity 实体
     *
     * @return 查询结果, 需和 {@link #pageProcessor} 支持类型一致
     */
    protected abstract Object queryWithPage(Page<T> condition, T entity);

}
