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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification 的一些方便方法
 * <br>
 * 2023-07 升级 Spring Data 2022 时删除了基于 Hibernate 内部 API 实现的渲染方法
 *
 * @author CofCool
 */
public class SpecificationUtils {

    public static <T, Y extends Comparable<? super Y>> Specification<T> andGreaterOrEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLessOrEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andBetween(Specification<T> sp, String property, Y minVal, Y maxVal) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(property), minVal, maxVal));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andNotEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLikeTo(Specification<T> sp, String property, Y val, boolean fullLike) {
        return sp.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(property), fullLike ? ("%" + val + "%") : (val + "%")));
    }

    public static <T, Y> Specification<T> andIn(Specification<T> sp, String property, List<Y> vals) {
        return sp.and((root, query, criteriaBuilder) -> {
            Path<Y> pro = root.get(property);
            CriteriaBuilder.In<Y> in = criteriaBuilder.in(pro);
            for (Y data : vals){
                in.value(data);
            }
            return criteriaBuilder.and(in);
        });
    }


    /**
     * 根据 Sort 生成 SQL 语句（不包括"order by"）
     * @param alias 别名
     * @param sort 排序
     * @return 排序语句
     */
    public static String renderSort(String alias, Sort sort) {
        StringBuilder sql = new StringBuilder();
        renderSort(alias, sort, sql);

        return sql.toString();
    }

    /**
     * 根据 Sort 生成 SQL 语句（不包括"order by"）
     * @param alias 别名
     * @param sort 排序
     * @param sql SQL 字符串
     */
    public static void renderSort(String alias, Sort sort, StringBuilder sql) {
        Iterator<Order> sortIterator = sort.iterator();
        while (sortIterator.hasNext()) {
            Order order = sortIterator.next();
            sql.append(alias)
                .append(".")
                .append(order.getProperty())
                .append(" ")
                .append(order.getDirection().toString());
            if (sortIterator.hasNext()) {
                sql.append(", ");
            }
        }
    }


}
