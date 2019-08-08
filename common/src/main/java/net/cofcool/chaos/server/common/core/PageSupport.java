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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 处理 page
 *
 * @author CofCool
 */
public class PageSupport {

    /**
     * 检查分页配置是否正确 <br/>
     * <p>
     *     <b>1. 页数</b>
     *     <ul>
     *         <li>添加默认值, 查看{@link Page}</li>
     *         <li>临界值处理</li>
     *     </ul>
     * </p>
     *
     * <p>
     *     <b>2. 页码</b>
     *     <ul>
     *         <li>页码减1</li>
     *         <li>页码小于等于0时置为0</li>
     *     </ul>
     * </p>
     *
     * @see Page
     */
    @Nonnull
    public static <T> Page<T> checkPage(@Nullable Page<T> pageCondition) {
        if (pageCondition == null) {
            pageCondition = new Page<>(Page.PAGE_NUMBER_FIRST_PAGE, Page.PAGE_SIZE);
        }

        if (pageCondition.getPageSize() <= 0) {
            pageCondition.setPageSize(Page.PAGE_SIZE);
        }

        if (pageCondition.getPageSize() < Page.PAGE_MIN_SIZE) {
            pageCondition.setPageSize(Page.PAGE_MIN_SIZE);
        }

        if (pageCondition.getPageSize() > Page.PAGE_MAX_SIZE) {
            pageCondition.setPageSize(Page.PAGE_MAX_SIZE);
        }

        if (pageCondition.getPageNumber() <= 0) {
            pageCondition.setPageNumber(1);
        }

        // 页码减1
        pageCondition.setPageNumber(pageCondition.getPageNumber() - 1);


        return pageCondition;
    }

}
