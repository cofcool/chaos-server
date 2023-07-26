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

package net.cofcool.chaos.server.extension;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageProcessor;
import net.cofcool.chaos.server.common.core.SimplePage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 适配 {@link org.springframework.data.domain.Page}
 *
 * @author CofCool
 */
public class Paging<T> extends SimplePage<T> {

    private static final long serialVersionUID = -1288144366898317311L;

    /**
     * 不推荐直接使用该构造方法
     */
    public Paging() {}

    private Paging(List<T> content) {
        super(content);
    }

    /**
     * 根据Spring Data的Page创建Page
     * @param page Page实例
     * @param <T> 模型类
     * @return Page实例
     */
    @Nonnull
    public static <T> Page<T> of(@Nonnull org.springframework.data.domain.Page<T> page) {
        return createPaging(page);
    }

    private static <T> Paging<T> createPaging(org.springframework.data.domain.Page<T> page) {
        Paging<T> mPage = new Paging<>(page.getContent());
        mPage.setFirstPage(page.isFirst());
        mPage.setLastPage(page.isLast());
        mPage.setTotal(page.getTotalElements());
        mPage.setPageNumber(page.getNumber());
        mPage.setPageSize(page.getSize());
        mPage.setPages(page.getTotalPages());

        return mPage;
    }

    /**
     * 获取 Pageable 实例
     * @return Pageable
     */
    public Pageable getPageable() {
        return getPageable(this);
    }

    /**
     * 获取 Pageable 实例
     */
    public Pageable getPageable(Sort sort) {
        return getPageable(this, sort);
    }

    /**
     * 获取 Pageable 实例
     */
    public static <T> Pageable getPageable(Page<T> page, Sort sort) {
        return getPageable(page.getPageNumber(), page.getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    public static <T> Pageable getPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    /**
     * 获取 Pageable 实例
     */
    public static <T> Pageable getPageable(int pageNumber, int pageSize, Sort sort) {
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    /**
     * 获取 Pageable 实例
     * @see org.springframework.data.domain.PageRequest
     */
    public static <T> Pageable getPageable(Page<T> page) {
        return getPageable(page.getPageNumber(), page.getPageSize());
    }

    /**
     * 获取分页处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> PageProcessor<T> getPageProcessor() {
        return (PageProcessor<T>) PAGE_PROCESSOR;
    }

    private static final PageProcessor<?> PAGE_PROCESSOR = new SpringPageProcessor<>();

    private static final class SpringPageProcessor<T> implements PageProcessor<T> {

        @SuppressWarnings("unchecked")
        @Override
        public Page<T> process(Page<T> condition, Object pageSomething) {
            Objects.requireNonNull(condition);
            Objects.requireNonNull(pageSomething);

            if (pageSomething instanceof org.springframework.data.domain.Page) {
                return of((org.springframework.data.domain.Page) pageSomething);
            } else if (pageSomething instanceof Page) {
                return (Page<T>) pageSomething;
            }


            throw new IllegalArgumentException("must be Page instance");
        }

    }

}
