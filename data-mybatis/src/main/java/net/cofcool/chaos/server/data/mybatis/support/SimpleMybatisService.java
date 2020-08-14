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

package net.cofcool.chaos.server.data.mybatis.support;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import java.io.Serializable;
import java.util.List;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SimpleMybatisService<T, ID extends Serializable, M extends BaseMapper<T>> extends
    SimpleService<T> implements InitializingBean {

    private M mapper;

    protected M getMapper() {
        return mapper;
    }

    @Autowired
    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        return mapper.selectPage(Paging.getIPage(condition), buildWrapper(condition, entity));
    }

    /**
     * 构建查询条件
     * @param entity 实体
     * @return Wrapper 实例
     */
    protected Wrapper<T> buildWrapper(Page<T> condition, T entity) {
        return new QueryWrapper<>(entity);
    }

    @Override
    public ExecuteResult<T> insert(T entity) {
        return convertResult(mapper.insert(entity), entity);
    }

    @Override
    public ResultState delete(T entity) {
        return SqlHelper.retBool(mapper.deleteById(getEntityId(entity))) ? ResultState.SUCCESSFUL : ResultState.FAILURE;
    }

    /**
     * 修改, 如果发现有旧数据则做修改操作, 否则做添加操作
     * @param entity 实体
     *
     * @return {@link ExecuteResult} 对象
     */
    @Override
    public ExecuteResult<T> update(T entity) {;
        return convertResult(mapper.updateById(entity), entity);
    }

    @Override
    public ExecuteResult<List<T>> queryAll(T entity) {
        return getConfiguration().getExecuteResult(
                mapper.selectList(new QueryWrapper<>(entity)),
                ResultState.SUCCESSFUL,
                ExceptionCodeDescriptor.SERVER_OK,
                ExceptionCodeDescriptor.SERVER_OK_DESC
        );
    }

    @Override
    public ExecuteResult<T> queryById(T entity) {
        T result = findById(entity);
        return convertResult(result == null ? 0 : 1, result);
    }

    /**
     * Mybatis int 返回值转换为 {@link ExecuteResult}
     *
     * @param result Mybatis 执行结果
     * @param entity 实体
     * @return {@link ExecuteResult} 对象
     */
    protected ExecuteResult<T> convertResult(int result, T entity) {
        if (result > 0) {
            return getConfiguration().getExecuteResult(
                entity,
                ResultState.SUCCESSFUL,
                ExceptionCodeDescriptor.SERVER_OK,
                ExceptionCodeDescriptor.SERVER_OK_DESC
            );
        } else {
            return getConfiguration().getExecuteResult(
                null,
                ResultState.FAILURE,
                ExceptionCodeDescriptor.OPERATION_ERR,
                ExceptionCodeDescriptor.OPERATION_ERR_DESC
            );
        }
    }

    /**
     * 根据实体的"ID"查询数据
     * @param entity 实体
     * @return 查询结果
     */
    protected T findById(T entity) {
        return mapper.selectById(getEntityId(entity));
    }

    /**
     * 读取实体主键
     */
    protected abstract ID getEntityId(T entity);

    @Override
    public void afterPropertiesSet() throws Exception {
        setPageProcessor(Paging.getPageProcessor());
    }
}
