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

    private Message<Map<String, Object>> message;

    /**
     * 查询结果封装
     *
     * @param page 分页数据
     */
    public SimpleQueryResult(@Nonnull Page<T> page, String code, String msg) {
        this(page, null, code, msg);
    }

    /**
     * 查询结果封装
     *
     * @param page 分页数据
     * @param ext 扩展信息
     */
    public SimpleQueryResult(Page<T> page, Object ext, String code, String msg) {
        Objects.requireNonNull(page);
        Objects.requireNonNull(page.getContent());

        this.page = page;
        this.ext = ext;
        this.message = Message.of(code, msg, createResultMap(page));
    }

    @Override
    public Page<T> page() {
        return page;
    }

    @Override
    public Object ext() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    private Map<String, Object> createResultMap(Page<T> page) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalRow", page.getTotal());
        result.put("list", page.getContent());
        result.put("firstPage", page.isFirstPage());
        result.put("lastPage", page.isLastPage());
        result.put("pageNumber", page.getPageNumber());
        result.put("pageSize", page.getPageSize());
        result.put("totalPage", page.getPages());
        result.put("ext", ext);

        return result;
    }

    @Override
    public Message<Map<String, Object>> result() {
        return message;
    }

}
