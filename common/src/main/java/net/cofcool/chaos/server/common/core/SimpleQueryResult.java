package net.cofcool.chaos.server.common.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * {@link QueryResult} 简单实现
 *
 * @author CofCool
 */
public class SimpleQueryResult<T> implements QueryResult<T, Map<String, Object>> {

    private static final long serialVersionUID = 3886864123428767070L;

    /**
     * 分页数据
     */
    private Page<T> page;

    /**
     * 扩展信息
     */
    private Object ext;

    /**
     * 查询结果封装
     *
     * @param page 分页数据
     */
    public SimpleQueryResult(@Nonnull Page<T> page) {
        this(page, null);
    }

    /**
     * 查询结果封装
     *
     * @param page 分页数据
     * @param ext 扩展信息
     */
    public SimpleQueryResult(Page<T> page, Object ext) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(page.getList());

        this.page = page;
        this.ext = ext;
    }

    @Override
    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    @Override
    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    private Map<String, Object> getResultMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalRow", page.getTotal());
        result.put("list", page.getList());
        result.put("firstPage", page.isFirstPage());
        result.put("lastPage", page.isLastPage());
        result.put("pageNumber", page.getPageNumber());
        result.put("pageSize", page.getPageSize());
        result.put("totalPage", page.getPages());
        result.put("ext", ext);

        return result;
    }

    @Override
    public Message<Map<String, Object>> getResult(String message) {
        return Message.successful(message, getResultMap());
    }

}
