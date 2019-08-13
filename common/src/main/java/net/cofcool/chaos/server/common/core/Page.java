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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 分页实体, 包含日期处理, 关键字等, 默认 pageNumber 为 0 时为第一页
 *
 * @author CofCool
 **/
public interface Page<T> extends Serializable {

    long MILLISECONDS_FLAG = Integer.MAX_VALUE;
    long MILLISECONDS_RATIO = 1000;

    int PAGE_SIZE = 15;
    int PAGE_MAX_SIZE = 200;
    int PAGE_MIN_SIZE = 1;
    int PAGE_NUMBER_FIRST_PAGE = 0;

    /**
     * 读取 <code>condition</code> 实例
     * @return  <code>condition</code> 实例
     */
    @Nullable
    T getCondition();

    /**
     * 配置 <code>condition</code>
     * @param val 新 <code>condition</code>
     */
    void setCondition(T val);

    /**
     * 列表数据
     * @return 列表数据
     */
    List<T> getContent();

    /**
     * 搜索关键字
     * @return 搜索关键字
     */
    String getWords();

    /**
     * 页码, 从 0 开始
     * @return 页码
     */
    int getPageNumber();

    /**
     * 每页数据数量
     * @return 每页数据数量
     */
    int getPageSize();

    /**
     * 数据总数量
     * @return 数据总数量
     */
    long getTotal();

    /**
     * 数据总页数
     * @return 数据总页数
     */
    int getPages();

    /**
     * 是否第一页
     * @return 是否第一页
     */
    boolean isFirstPage();

    /**
     * 是否最后一页
     * @return 是否最后一页
     */
    boolean isLastPage();

    /**
     * 开始日期时间戳, 单位为"ms"
     * @return 开始日期
     */
    long getStartDate();

    /**
     * 结束日期时间戳, 单位为"ms"
     * @return 结束日期
     */
    long getEndDate();

    /**
     * 检查分页配置是否正确, 包括页数和页码
     */
    void checkPage();

    /**
     * 时间戳转换为 {@link Date}, 支持"s", "ms"
     * @param date 时间戳
     * @return {@link Date} 实例
     */
    static Date convertToDate(long date) {
        if (date > MILLISECONDS_FLAG) {
            return new Date(date);
        } else {
            return new Date(date * MILLISECONDS_RATIO);
        }
    }

    /**
     * 创建空列表 {@link Page}
     * @param <T> 类型
     * @return {@link Page} 实例
     */
    static <T> Page<T> empty() {
        return SimplePage.empty();
    }

    /**
     * 创建默认配置的 {@link Page} 实例
     * @param <T> 类型
     * @return {@link Page} 实例
     */
    static <T> Page<T> emptyPageForRequest() {
        return new SimplePage<>(Page.PAGE_NUMBER_FIRST_PAGE, Page.PAGE_SIZE);
    }

    /**
     * 检查分页配置是否正确, 包括页数和页码
     * @param pageCondition 原 {@link Page} 实例
     * @param <T> 类型
     * @return 处理后的 {@link Page} 实例
     */
    @Nonnull
    static <T> Page<T> checkPage(@Nullable Page<T> pageCondition) {
        if (pageCondition == null) {
            pageCondition = emptyPageForRequest();
        }

        pageCondition.checkPage();

        return pageCondition;
    }

}
