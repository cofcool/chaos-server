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
     *
     * @param query
     * @return
     */
    List<T> findAll(Query query);

    /**
     *
     * @param query
     * @param pageable
     * @return
     */
    Page<T> findAll(Query query, Pageable pageable);

    <S extends T> boolean update(S condition, UpdateDefinition update);

    boolean update(Query query, UpdateDefinition update);

    <S extends T> boolean updateById(S entity);

    boolean updateById(ID id, UpdateDefinition update);
}
