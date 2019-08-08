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

package net.cofcool.chaos.server.demo.api;

import java.util.List;
import lombok.Data;

/**
 * 仅用来生成文档
 */
@Data
public class QueryMessage<T> {

    /**
     * 总行数
     */
    private long totalRow;
    /**
     * 数据
     */
    private List<T> list;
    /**
     * 是否第一页
     */
    private boolean firstPage;
    /**
     * 是否最后一页
     */
    private boolean lastPage;
    /**
     * 页码
     */
    private int pageNumber;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;

}
