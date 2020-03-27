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

package net.cofcool.chaos.server.data.jpa.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.persistence.Transient;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageProcessor;
import net.cofcool.chaos.server.common.core.SimplePage;
import net.cofcool.chaos.server.common.util.BeanUtils;
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
     * 当 <code>Page</code> 为联表分页结果时, 本方法可合并分页数据中的多个元素到 <code>primaryBean</code>。
     * 被合并的元素在 <code>primaryBean</code>中必须有相应的 <code>getter</code> 方法, 并且使用 {@link Transient} 标注, 才可触发合并操作。<br>
     *
     * 注意：<code>primaryBean</code> 需有 <code>getter</code> 和 <code>setter</code> 方法
     *
     * @param page page
     * @param primaryBean 主类
     * @param <T> 元素类型
     * @return page
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Page<T> of(org.springframework.data.domain.Page<List<T>> page, Class<T> primaryBean) {
        Paging mPage = createPaging(page);
        List mPageList = mPage.getContent();
        if (mPageList.isEmpty()) {
            return mPage;
        }

        Map<String, Class> propertyTypes = new HashMap<>();
        for (Method method : primaryBean.getMethods()) {
            if (method.getAnnotation(Transient.class) != null) {
                propertyTypes.put(method.getName(), method.getReturnType());
            }
        }

        if (propertyTypes.isEmpty()) {
            return mPage;
        }

        List mutableList = new ArrayList<>(mPageList.size());

        Map<Class, Object> classValueMap = new HashMap<>();
        for (int i = 0; i < mPageList.size(); i++) {
            Object item = mPageList.get(i);
            // 连表时有多个结果的话, item类型为数组, 即Object[]
            if (item.getClass().isArray()) {
                Object[] result = (Object[]) item;
                for (Object cItem : result) {
                    if (cItem != null) {
                        classValueMap.put(cItem.getClass(), cItem);
                    }
                }
            }

            Object primaryObj = classValueMap.get(primaryBean);
            propertyTypes.forEach((key, val) -> {
                try {
                    primaryBean.getMethod(BeanUtils.getterToSetter(key), val).invoke(primaryObj, classValueMap.get(val));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
                }
            });
            mutableList.add(primaryObj);

            classValueMap.clear();
        }

        mPage.setContent(Collections.unmodifiableList(mutableList));

        return mPage;
    }

    /**
     * 获取 Pageable 实例
     * @return Pageable
     *
     * @see JpaPage
     */
    public Pageable getPageable() {
        return new JpaPage(getPageNumber(), getPageSize());
    }

    /**
     * 获取 Pageable 实例
     */
    public Pageable getPageable(Sort sort) {
        return getPageable(getPageNumber(), getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    public Pageable getPageable(Integer pageNumber, Integer pageSize, Sort sort) {
        return new JpaPage(getPageNumber(), getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    public static Pageable getPageable(Page page, Sort sort) {
        return new JpaPage(page.getPageNumber(), page.getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    public static Pageable getPageable(Page page) {
        return new JpaPage(page.getPageNumber(), page.getPageSize());
    }

    /**
     * 获取分页处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> PageProcessor<T> getPageProcessor() {
        return (PageProcessor<T>) PAGE_PROCESSOR;
    }

    private static final PageProcessor<?> PAGE_PROCESSOR = new JpaPageProcessor<>();

    private static final class JpaPageProcessor<T> implements PageProcessor<T> {

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
