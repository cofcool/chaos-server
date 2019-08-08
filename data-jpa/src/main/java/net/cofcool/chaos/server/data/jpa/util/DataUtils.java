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

package net.cofcool.chaos.server.data.jpa.util;

import java.util.List;
import java.util.Optional;
import net.cofcool.chaos.server.data.jpa.support.Paging;
import org.springframework.data.domain.PageImpl;

/**
 * @author CofCool
 */
public class DataUtils {

    /**
     * 处理连表结果, 适用于单条数据, 多条分页数据参考 {@link Paging}
     * <br>
     *
     * @see Paging
     * @return primaryBean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> mergeJoinResult(List<T> result, Class<T> primaryBean) {
        List<T> resultData = Paging.of(new PageImpl<>((List<List<T>>) result), primaryBean).getList();
        if (resultData.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultData.get(0));
        }
    }

    /**
     * 处理连表结果, 适用于多条数据, 分页数据参考 {@link Paging}
     * <br>
     *
     * @see Paging
     * @return primaryBean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> mergeMultiJoinResult(List<T> result, Class<T> primaryBean) {
        return Paging.of(new PageImpl<>((List<List<T>>) result), primaryBean).getList();
    }

}
