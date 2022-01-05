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
import java.util.Optional;
import net.cofcool.chaos.server.common.core.DataAccess;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import net.cofcool.chaos.server.extension.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.UntypedExampleMatcher;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 基于 {@link MongoRepositoryExtension} 的简单 {@link DataAccess} 实现
 *
 * @param <T> 实体
 * @param <ID> 实体的 ID
 * @param <R> 实体对应的 MongoRepository
 * @author CofCool
 */
public abstract class SimpleMongoService<T, ID, R extends MongoRepositoryExtension<T, ID>> extends SimpleService<T> implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private R mongoRepository;

    private MongoTemplate mongoTemplate;

    /**
     * @return {@link MongoTemplate} 实例
     */
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @return 当前实体对应的 {@link MongoRepository}
     */
    protected R getMongoRepository() {
        return mongoRepository;
    }

    @Autowired
    public void setMongoRepository(R mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public ExecuteResult<T> insert(T entity) {
        return getConfiguration().getExecuteResult(
            mongoRepository.insert(entity),
            ResultState.SUCCESSFUL,
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    @Override
    public ExecuteResult<T> save(T entity) {
        return getConfiguration().getExecuteResult(
            mongoRepository.save(entity),
            ResultState.SUCCESSFUL,
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    @Override
    public ResultState delete(T entity) {
        Optional<T> result = findById(entity);
        if (result.isPresent()) {
            mongoRepository.delete(result.get());
            return ResultState.SUCCESSFUL;
        } else {
            return ResultState.FAILURE;
        }
    }

    @Override
    public ExecuteResult<T> update(T entity) {
        ExecuteResult<T> result = queryById(entity);
        if (!result.successful()) {
            return result;
        }

        boolean update = mongoRepository.updateById(entity);
        if (update) {
            return getConfiguration().getExecuteResult(
                null,
                ResultState.SUCCESSFUL,
                ExceptionCodeDescriptor.SERVER_OK
            );
        } else {
            return getConfiguration().getExecuteResult(
                null,
                ResultState.FAILURE,
                ExceptionCodeDescriptor.OPERATION_ERR
            );
        }
    }

    @Override
    public ExecuteResult<List<T>> queryAll(T entity) {
        return getConfiguration().getExecuteResult(
            mongoRepository.findAll(getExample(entity)),
            ResultState.SUCCESSFUL,
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    @Override
    public ExecuteResult<T> queryById(T entity) {
        Optional<T> data = findById(entity);
        return data
            .map(t ->
                getConfiguration().getExecuteResult(
                    t,
                    ResultState.SUCCESSFUL,
                    ExceptionCodeDescriptor.SERVER_OK
                )
            )
            .orElseGet(() ->
                getConfiguration().getExecuteResult(
                    null,
                    ResultState.FAILURE,
                    ExceptionCodeDescriptor.DATA_ERROR
                )
            );
    }

    /**
     * 根据实体的"ID"查询数据
     * @param entity 实体
     * @return 查询结果
     */
    protected Optional<T> findById(T entity) {
        return mongoRepository.findById(mongoRepository.getRequiredId(entity));
    }

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        return mongoRepository.findAll(getExample(entity), Paging.getPageable(condition));
    }

    /**
     * 获取 {@link Example}, 子类可自定义采用 {@link ExampleMatcher} 还是 {@link UntypedExampleMatcher}
     * @param entity 实体
     * @return {@link Example}
     */
    protected Example<T> getExample(T entity) {
        return Example.of(entity);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setPageProcessor(Paging.getPageProcessor());
    }
}
