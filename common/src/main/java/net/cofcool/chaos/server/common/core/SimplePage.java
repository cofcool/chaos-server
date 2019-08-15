/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.common.core;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.ToString;

/**
 * 分页实体, 包含日期处理, 关键字等, 默认 pageNumber 为 0 时为第一页
 *
 * @author CofCool
 **/
@ToString
public class SimplePage<T> implements Page<T> {

    private static final long serialVersionUID = -76703640096908564L;

    private static final Page<?> EMPTY_PAGE = new SimplePage<>(Collections.emptyList());

    private long startDate;

    private long endDate;

    private int pageNumber;

    private int pageSize;

    private long total;
    private int pages;

    private boolean isFirstPage;
    private boolean isLastPage;

    private List<T> content;

    private String words;

    private T condition;

    @Nullable
    @Override
    public T getCondition() {
        return condition;
    }

    @Override
    public void setCondition(T condition) {
        this.condition = condition;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    protected void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public int getPages() {
        return pages;
    }

    @Override
    public boolean isFirstPage() {
        return isFirstPage;
    }

    @Override
    public boolean isLastPage() {
        return isLastPage;
    }

    @Override
    public long getStartDate() {
        return startDate;
    }

    @Override
    public long getEndDate() {
        return endDate;
    }

    @Override
    public String getWords() {
        return words;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public void checkPage() {
        if (pageSize < Page.PAGE_MIN_SIZE || pageSize > Page.PAGE_MAX_SIZE) {
            pageSize = Page.PAGE_SIZE;
        }

        if (pageNumber < 0) {
            pageNumber = PAGE_NUMBER_FIRST_PAGE;
        }
    }

    /**
     * 创建空列表Page实例
     * @param <T> 类型
     * @return Page
     */
    @SuppressWarnings("unchecked")
    public static<T> Page<T> empty() {
        return (Page<T>) EMPTY_PAGE;
    }

    /**
     * 不推荐直接使用该构造方法
     */
    public SimplePage() {}

    protected SimplePage(List<T> content) {
        this.content = content;
    }

    public SimplePage(int pageNumber, int pageSize) {
        this(pageNumber, pageSize, 0L, 0L);
    }

    public SimplePage(int pageNumber, int pageSize, long startDate, long endDate) {
        this(pageNumber, pageSize, startDate, endDate, null);
    }

    public SimplePage(int pageNumber, int pageSize, long startDate, long endDate, String words) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.words = words;
    }

}
