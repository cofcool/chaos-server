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

package net.cofcool.chaos.server.common.core;

import net.cofcool.chaos.server.common.core.Result.ResultState;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 基础配置支持
 *
 * @author CofCool
 *
 * @see ExceptionCodeManager
 * @see Message
 * @see Result
 */
public class ConfigurationSupport implements InitializingBean {

    private ExceptionCodeManager exceptionCodeManager;

    private boolean isDebug;

    public ConfigurationSupport() {
    }

    public void setExceptionCodeManager(ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 获取 {@link ExceptionCodeManager}
     * @return {@link ExceptionCodeManager}
     */
    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    /**
     * 创建 {@link Message} 实例，应用可创建自定义 {@link Message}
     * @param code 状态码
     * @param msg 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return {@link Message} 实例
     */
    protected <T> Message<T> creatingMessage(String code, String msg, T data) {
        return Message.of(code, msg, data);
    }

    /**
     * 创建 {@code ExecuteResult} 实例
     *
     * @param entity 结果数据
     * @param state 执行状态
     * @param codeKey 描述码
     * @param msgKey 描述信息
     * @param <T> 结果类型
     * @return ExecuteResult 实例
     */
    public <T> ExecuteResult<T> getExecuteResult(T entity, ResultState state, String codeKey, String msgKey) {
        return ExecuteResult.of(
            state,
            getMessageByKey(codeKey, msgKey, entity)
        );
    }

    /**
     * 创建 {@link QueryResult} 实例
     *
     * @param page 分页数据
     * @param <T> 数据类型
     * @param code 描述码
     * @param msg 描述信息
     * @return {@link QueryResult} 实例
     */
    public <T> QueryResult<T, ?> getQueryResult(Page<T> page, String code, String msg) {
        return QueryResult.of(
            getMessageByKey(code, msg, page)
        );
    }

    /**
     * 创建 {@link Message} 实例
     * @param code 状态码
     * @param msg 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return {@link Message} 实例
     */
    public <T> Message<T> getMessage(String code, String msg, T data) {
        return creatingMessage(code, msg, data);
    }

    /**
     * 创建 {@link Message} 实例, 通过 {@link ExceptionCodeManager} 获取对应的描述信息来创建
     * @param code 状态码
     * @param codeIsKey {@code code} 是否为 {@link ExceptionCodeManager} 的 {@code key}, 如果是则作为 {@code key} 获取对应的描述信息
     * @param msg 描述信息
     * @param msgIsKey {@code msg} 是否为 {@link ExceptionCodeManager} 的 {@code key}, 如果是则作为 {@code key} 获取对应的描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return {@link Message} 实例
     */
    public <T> Message<T> getMessage(String code, boolean codeIsKey, String msg, boolean msgIsKey, T data) {
        return creatingMessage(
            codeIsKey ? getExceptionCode(code) : code,
            msgIsKey ? getExceptionDescription(msg) : msg,
            data);
    }

    /**
     * 创建 {@link Message} 实例, 通过 {@link ExceptionCodeManager} 获取对应的描述信息来创建
     * @param codeKey 状态码
     * @param msgKey 描述信息
     * @param data 携带数据
     * @param <T> 携带数据类型
     * @return {@link Message} 实例
     */
    public <T> Message<T> getMessageByKey(String codeKey, String msgKey, T data) {
        return creatingMessage(
            getExceptionCode(codeKey),
            getExceptionDescription(msgKey),
            data
        );
    }

    /**
     * 获取异常描述码
     * @param key 异常类型
     * @return 异常描述码
     */
    public String getExceptionCode(String key) {
        return getExceptionCodeManager().getCode(key);
    }

    /**
     * 获取异常描述信息
     * @param key 异常类型
     * @return 异常描述信息
     */
    public String getExceptionDescription(String key) {
        return getExceptionCodeManager().getDescription(key);
    }

    /**
     * 是否调试模式
     * @return 是否调试模式
     */
    public boolean isDebug() {
        return isDebug;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(exceptionCodeManager, "exceptionCodeManager must be specified");
    }
}
