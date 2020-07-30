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

package net.cofcool.chaos.server.data.mybatis.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageProcessor;
import net.cofcool.chaos.server.common.core.SimplePage;

/**
 * 适配 {@link com.baomidou.mybatisplus.extension.plugins.pagination.Page}
 *
 * @author CofCool
 */
public class Paging<T> extends SimplePage<T> {

    private static final long serialVersionUID = 6040593186072235603L;

    /**
     * 不推荐直接使用该构造方法
     */
    public Paging() {}

    private Paging(List<T> content) {
        super(content);
    }

    /**
     * 根据 IPage 创建 Page
     * @param page IPage 实例
     * @param <T> 数据类型
     * @return Page实例
     */
    @Nonnull
    public static <T> Page<T> of(@Nonnull IPage<T> page) {
        Paging<T> mPage = new Paging<>(page.getRecords());
        mPage.setTotal(page.getTotal());
        mPage.setPageNumber((int) page.getCurrent());
        mPage.setPageSize((int) page.getSize());
        mPage.setPages((int) page.getPages());

        return mPage;
    }

    /**
     * 获取 {@link IPage} 实例
     * @param page page 实例
     * @param <T> 数据类型
     * @param orders 排序字段信息
     * @return IPage 实例
     */
    public static <T> IPage<T> getIPage(Page<T> page, @Nullable List<OrderItem> orders) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> mPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        mPage.setCurrent(page.getPageNumber());
        mPage.setSize(page.getPageSize());
        mPage.setOrders(orders);

        return mPage;
    }

    /**
     * 获取 IPage 实例
     * @param page page 实例
     * @param <T> 数据类型
     * @return IPage 实例
     */
    public static <T> IPage<T> getIPage(Page<T> page) {
        return getIPage(page, null);
    }


    /**
     * 获取分页处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> PageProcessor<T> getPageProcessor() {
        return (PageProcessor<T>) PAGE_PROCESSOR;
    }

    private static final PageProcessor<?> PAGE_PROCESSOR = new MybatisPageProcessor<>();

    private static final class MybatisPageProcessor<T> implements PageProcessor<T> {

        @SuppressWarnings("unchecked, rawtypes")
        @Override
        public Page<T> process(Page<T> condition, Object pageSomething) {
            Objects.requireNonNull(condition);
            Objects.requireNonNull(pageSomething);

            if (pageSomething instanceof IPage) {
                return (Page<T>) Paging.of((IPage)pageSomething);
            } else if (pageSomething instanceof Page) {
                return (Page<T>) pageSomething;
            }

            throw new IllegalArgumentException("must be com.baomidou.mybatisplus.core.metadata.IPage instance");
        }

    }
}
