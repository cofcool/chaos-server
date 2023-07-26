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
package net.cofcool.chaos.server.extension.mongo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 增强 {@link MongoRepository}
 * @param <T> 实体
 * @param <ID> ID
 */
@NoRepositoryBean
public interface MongoRepositoryExtension<T, ID>  extends MongoRepository<T, ID> {

    /**
     * 根据 {@link Query} 查询
     * @param query 条件
     * @return 查询结果
     */
    List<T> findAll(Query query);

    /**
     * 根据 {@link Query} 分页查询
     * @param query 条件
     * @param pageable 分页, 会覆盖 Query 的分页配置
     * @return 查询结果
     */
    Page<T> findAll(Query query, Pageable pageable);

    /**
     * 更新
     * @param condition 通过 {@link org.springframework.data.domain.Example} 注入到 Query 中
     * @param update 更新数据
     * @param <S> 实体类型
     * @return 是否更新
     */
    <S extends T> boolean update(S condition, UpdateDefinition update);

    /**
     * 更新
     * @param query 更新时的查询条件
     * @param update 更新数据
     * @return 是否更新
     */
    boolean update(Query query, UpdateDefinition update);

    /**
     * 根据 ID 更新
     * @param entity 更新数据, 遍历实体属性元数据生成更新数据
     * @param <S> 实体类型
     * @return 是否更新
     * @apiNote 本方法通过反射传入的对象获取各个属性的值, 因此不支持更新嵌套对象部分属性
     */
    <S extends T> boolean updateById(S entity);

    /**
     * 根据 ID 更新
     * @param id 实体 ID
     * @param update 更新数据
     * @return 是否更新
     */
    boolean updateById(ID id, UpdateDefinition update);

    /**
     * 读取 Entity 对应的 ID
     * @param entity 实体
     * @param <S> 实体类型
     * @return Entity 对应的 ID
     */
    <S extends T> ID getId(S entity);

    /**
     * 读取 Entity 对应的 ID
     * @param entity 实体
     * @param <S> 实体类型
     * @return Entity 对应的 ID
     * @throws IllegalArgumentException 如果 ID 为 null 抛出异常
     */
    <S extends T> ID getRequiredId(S entity);
}
