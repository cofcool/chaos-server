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

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageProcessor;
import net.cofcool.chaos.server.common.core.SimplePage;

/**
 * 适配 {@link PageHelper}
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
     * 根据 PageHelper 的 PageInfo 创建Page
     * @param page PageInfo实例
     * @param <T> 模型类
     * @return Page实例
     */
    @Nonnull
    public static <T> Page<T> of(@Nonnull PageInfo<T> page) {
        Paging<T> mPage = new Paging<>(page.getList());
        mPage.setFirstPage(page.isIsFirstPage());
        mPage.setLastPage(page.isIsLastPage());
        mPage.setTotal(page.getTotal());
        mPage.setPageNumber(page.getPageNum());
        mPage.setPageSize(page.getPageSize());
        mPage.setPages(page.getPages());

        return mPage;
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

        @SuppressWarnings("unchecked")
        @Override
        public Page<T> process(Page<T> condition, Object pageSomething) {
            Objects.requireNonNull(condition);
            Objects.requireNonNull(pageSomething);

            if (pageSomething instanceof ISelect) {
                return Paging.of(
                    PageHelper.startPage(
                        condition.getPageNumber(), condition.getPageSize()
                    ).doSelectPageInfo((ISelect) pageSomething)
                );
            } else if (pageSomething instanceof Page) {
                return (Page<T>) pageSomething;
            }

            throw new IllegalArgumentException("must be ISelect instance");
        }

    }
}
