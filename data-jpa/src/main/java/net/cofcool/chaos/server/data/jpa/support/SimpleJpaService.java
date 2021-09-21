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

package net.cofcool.chaos.server.data.jpa.support;

import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import net.cofcool.chaos.server.common.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.core.EntityInformation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 {@link JpaRepository} 的简单 {@link net.cofcool.chaos.server.common.core.DataAccess} 实现
 *
 * @param <T> 实体
 * @param <ID> 实体的 ID
 * @param <J> 实体对应的 JpaRepository
 * @author CofCool
 */
public abstract class SimpleJpaService<T, ID, J extends JpaRepository<T, ID>> extends SimpleService<T> implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final Map<Class<?>, EntityInformation<?, ?>> CACHED_ENTITY_INFORMATION = new ConcurrentHashMap<>();

    private J jpaRepository;

    private EntityManager entityManager;

    /**
     * 获取当前实体对应的 {@link JpaRepository}
     * @return 当前实体对应的 {@link JpaRepository}
     */
    protected J getJpaRepository() {
        return jpaRepository;
    }

    @Autowired
    public void setJpaRepository(J jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * 获取 {@link EntityManager}
     * @return {@link EntityManager}
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        return jpaRepository.findAll(Paging.getPageable(condition));
    }

    @Override
    public ExecuteResult<T> insert(T entity) {
        return getConfiguration().getExecuteResult(
            jpaRepository.save(entity),
            ResultState.SUCCESSFUL,
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    @Override
    public ResultState delete(T entity) {
        Optional<T> result = findById(entity);
        if (result.isPresent()) {
            jpaRepository.delete(result.get());
            return ResultState.SUCCESSFUL;
        } else {
            return ResultState.FAILURE;
        }
    }

    /**
     * {@inheritDoc}, 方法返回值中的 entity 为传入的 entity
     */
    @Override
    public ExecuteResult<T> update(T entity) {
        Optional<T> result = findById(entity);
        if (!result.isPresent()) {
            return getConfiguration().getExecuteResult(
                null,
                ResultState.FAILURE,
                ExceptionCodeDescriptor.DATA_ERROR
            );
        }

        T dirtyEntity = result.get();

        JpaEntityInformation<T, ID> information = getEntityInformation(entity.getClass());
        ManagedType<? super T> declaringType = information.getIdAttribute().getDeclaringType();
        declaringType.getAttributes().forEach(a -> {
            final Member member = a.getJavaMember();
            if (member instanceof Field) {
                try {
                    Object val = ((Field) member).get(entity);
                    if (val != null) {
                        ((Field) member).set(dirtyEntity, val);
                    }
                } catch (IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("SimpleJpaService#update set field error", e);
                    }
                }
            } else if (member instanceof Method) {
                try {
                    Object val = ((Method) member).invoke(entity);
                    if (val != null) {
                        String memberName = member.getName();
                        member.getDeclaringClass().getMethod(BeanUtils.getterToSetter(memberName), val.getClass()).invoke(dirtyEntity, val);
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("SimpleJpaService#update invoke property method error", e);
                    }
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("SimpleJpaService#update do not support property attribute is {} type", member);
                }
            }
        });

        jpaRepository.save(dirtyEntity);

        return getConfiguration().getExecuteResult(
            entity,
            ResultState.SUCCESSFUL,
            ExceptionCodeDescriptor.SERVER_OK
        );
    }

    @Override
    public ExecuteResult<List<T>> queryAll(T entity) {
        return getConfiguration().getExecuteResult(
            jpaRepository.findAll(Example.of(entity)),
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
        return jpaRepository.findById(getEntityId(entity));
    }

    /**
     * 获取实体主键
     *
     * @see EntityInformation#getRequiredId(Object)
     */
    protected ID getEntityId(T entity) {
        return getEntityInformation(entity.getClass()).getRequiredId(entity);
    }

    /**
     * 获取 {@link JpaEntityInformation}
     * @param domainClass 实体类型
     * @return {@link JpaEntityInformation}
     *
     * @see org.springframework.data.jpa.repository.support.JpaRepositoryFactory
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getEntityInformation(Class)
     */
    @SuppressWarnings("unchecked")
    protected JpaEntityInformation<T, ID> getEntityInformation(Class<?> domainClass) {
        return (JpaEntityInformation<T, ID>) CACHED_ENTITY_INFORMATION
                .computeIfAbsent(
                        domainClass,
                        k -> JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager)
                );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setPageProcessor(Paging.getPageProcessor());
    }

}
