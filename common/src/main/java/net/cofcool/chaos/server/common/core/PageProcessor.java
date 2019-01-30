package net.cofcool.chaos.server.common.core;

/**
 * 分页处理
 *
 * @author CofCool
 */
public interface PageProcessor<T> {

    /**
     * 分页处理
     * @param condition 条件
     * @param pageSomething 分页相关数据
     * @return 分页结果
     */
    Page<T> process(Page<T> condition, Object pageSomething);

}