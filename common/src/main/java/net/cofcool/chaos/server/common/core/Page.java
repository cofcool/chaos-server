package net.cofcool.chaos.server.common.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.util.BeanUtils;

/**
 * 分页实体, 包含日期处理, 关键字等, 默认 pageNumber 为 0 时为第一页
 *
 * @author CofCool
 **/
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -76703640096908564L;

    private static final long MILLISECONDS_FLAG = Integer.MAX_VALUE;
    private static final long MILLISECONDS_RATIO = 1000;

    public static final int PAGE_SIZE = 15;

    public static final int PAGE_MAX_SIZE = 200;

    public static final int PAGE_MIN_SIZE = 1;

    public static final int PAGE_NUMBER_FIRST_PAGE = 1;

    private static final Page EMPTY_PAGE = new Page<>(Collections.emptyList());

    private Long startDate;

    private Long endDate;

    private int queryFlag;

    private int pageNumber;

    private int pageSize;

    private long total;
    private int pages;

    private boolean isFirstPage;
    private boolean isLastPage;

    private List<T> list;

    private String wd;

    private T condition;

    /**
     * 仅用数据权限拦截
     */
    private Map<String, Object> authIdsMap;

    @Deprecated
    public Page() {}

    /**
     * 创建空列表Page实例, 推荐使用, 不建议使用 {@link #Page()}
     * @param <T> 类型
     * @return Page
     */
    @SuppressWarnings("unchecked")
    public static<T> Page<T> empty() {
        return (Page<T>) EMPTY_PAGE;
    }

    public Page(Integer pageNumber, Integer pageSize) {
        this(pageNumber, pageSize, null, null);
    }

    protected Page(List<T> content) {
        this.list = content;
    }

    public Page(Integer pageNumber, Integer pageSize, Long startDate, Long endDate, int queryFlag) {
        this(pageNumber, pageSize, startDate, endDate, queryFlag, null);
    }

    public Page(Integer pageNumber, Integer pageSize, Long startDate, Long endDate, int queryFlag, String wd) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.startDate = startDate;
        this.endDate = endDate;
        this.queryFlag = queryFlag;
        this.wd = wd;
    }

    public Page(Integer pageNumber, Integer pageSize, Long startDate, Long endDate, String wd) {
        this(pageNumber, pageSize, startDate, endDate, 0, wd);
    }

    public Page(Integer pageNumber, Integer pageSize, Long startDate, Long endDate) {
        this(pageNumber, pageSize, startDate, endDate, null);
    }

    /**
     * 获取T类型的condition实例
     * @return condition实例
     */
    @Nullable
    public T getCondition() {
        return condition;
    }

    private void setupConditionProps(T originCondition) {
        if (authIdsMap != null) {
            authIdsMap.forEach((key, value) -> {
                try {
                    BeanUtils.getPropertyDescriptor(originCondition.getClass(), key).getWriteMethod().invoke(originCondition, value);
                } catch (Exception ignore) {

                }
            });
        }
    }

    /**
     * 获取T类型的condition实例, 如果为null, 则会创建
     * @param clazz T
     * @return condition实例
     */
    public T getCondition(Class<T> clazz) {
        if (condition == null) {
            try {
                condition = clazz.getConstructor().newInstance();
            } catch (Exception ignore) {
                throw new IllegalArgumentException("create condition instance failure");
            }
        }

        setupConditionProps(condition);

        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public List<T> getList() {
        return list;
    }

    protected void setList(List<T> list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Date startDate() {
        if (startDate == null) {
            return null;
        }
        return getDate(startDate);
    }

    public Date endDate() {
        if (endDate == null) {
            return null;
        }
        return getDate(endDate);
    }

    private Date getDate(long date) {
        if (date > MILLISECONDS_FLAG) {
            return new Date(date);
        } else {
            return new Date(date * MILLISECONDS_RATIO);
        }
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public int getQueryFlag() {
        return queryFlag;
    }

    public String getWd() {
        return wd;
    }

    public void setAuthIdsMap(Map<String, Object> authIdsMap) {
        this.authIdsMap = authIdsMap;
    }

    /**
     * 设置需要注入到condition的key-value
     */
    public void putMappedValue(String key, Object value) {
        if (authIdsMap == null) {
            authIdsMap = new HashMap<>();
        }

        authIdsMap.put(key, value);
    }

}
