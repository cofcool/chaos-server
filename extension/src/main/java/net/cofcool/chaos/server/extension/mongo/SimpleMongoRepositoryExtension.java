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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.hibernate.validator.internal.util.ConcurrentReferenceHashMap;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * 增强 {@link org.springframework.data.mongodb.repository.MongoRepository} 实现
 * @author CofCool
 */
public class SimpleMongoRepositoryExtension<T, ID>  extends SimpleMongoRepository<T, ID> implements MongoRepositoryExtension<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;
    private final Map<Class<?>, List<Field>> fieldsCache = new ConcurrentReferenceHashMap<>(8);

    public SimpleMongoRepositoryExtension(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public List<T> findAll(Query query) {
        return mongoOperations.find(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");

        List<T> list = findAll(query.with(pageable));

        return PageableExecutionUtils.getPage(
                list,
                pageable,
                () -> mongoOperations.count(Query.of(query).limit(-1).skip(-1), entityInformation.getJavaType(), entityInformation.getCollectionName())
        );
    }

    @Override
    public <S extends T> boolean update(S condition, UpdateDefinition update) {
        return update(Query.query(Criteria.byExample(Example.of(condition))), update);
    }

    @Override
    public boolean update(Query query, UpdateDefinition update) {
        return mongoOperations.updateMulti(query, update, entityInformation.getJavaType()).getModifiedCount() >= 1;
    }

    @Override
    public <S extends T> boolean updateById(S entity) {
        ID id = entityInformation.getRequiredId(entity);
        String idAttribute = entityInformation.getIdAttribute();

        Update definition = Update.fromDocument(Document.parse("{}"));
        fieldsCache
            .computeIfAbsent(entity.getClass(), k -> {
                List<Field> fields = new ArrayList<>();
                ReflectionUtils.doWithFields(
                    k,
                    f -> {
                        f.setAccessible(true);
                        fields.add(f);
                    },
                    field -> !field.getName().equals(idAttribute)
                        || !AnnotatedElementUtils.hasAnnotation(field, Transient.class)
                );

                return fields;
            })
            .forEach(f -> {
                Object value = ReflectionUtils.getField(f, entity);
                if (value != null) {
                    definition.set(f.getName(), value);
                }
            });

        return updateById(id, definition);
    }

    @Override
    public boolean updateById(ID id, UpdateDefinition update) {
        return mongoOperations
            .updateFirst(
                new Query(Criteria.where(entityInformation.getIdAttribute()).is(id)),
                update,
                entityInformation.getJavaType()
            )
            .getModifiedCount() == 1;
    }

    @Override
    public <S extends T> ID getId(S entity) {
        return entityInformation.getId(entity);
    }

    @Override
    public <S extends T> ID getRequiredId(S entity) {
        return entityInformation.getRequiredId(entity);
    }

}
