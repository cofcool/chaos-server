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

import java.util.List;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public abstract class SimpleMybatisService<T, ID, M extends Mapper<T>> extends
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
    public ExecuteResult<List<T>> queryAll(T entity) {
        return ExecuteResult.of(
            getMapper()
                .selectByExample(Example.builder(entity.getClass()).build()),
            ResultState.SUCCESSFUL,
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

}
