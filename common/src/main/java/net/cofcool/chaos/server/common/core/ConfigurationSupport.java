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

import lombok.Builder;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import org.springframework.util.Assert;

/**
 * 基础配置支持, 包括 {@link ExceptionCodeManager}, {@link Message}, {@link Result}等,
 * 应用可通过 {@link ConfigurationCustomizer} 自定义 {@link Message} 等实现方式等
 *
 * @see ExceptionCodeManager
 * @see Message
 * @see Result
 * @see ConfigurationCustomizer
 *
 * @author CofCool
 */
@Builder
public class ConfigurationSupport {

    /**
     * 异常描述管理
     */
    private final ExceptionCodeManager exceptionCodeManager;

    /**
     * 是否调试模式
     */
    private final boolean isDebug;

    /**
     *  自定义配置项
     */
    private final ConfigurationCustomizer customizer;

    protected ConfigurationSupport(
        ExceptionCodeManager exceptionCodeManager, boolean isDebug,
        ConfigurationCustomizer customizer) {
        Assert.notNull(exceptionCodeManager, "exceptionCodeManager must be specified");
        Assert.notNull(customizer, "customizer must be specified");

        this.exceptionCodeManager = exceptionCodeManager;
        this.isDebug = isDebug;
        this.customizer = customizer;
    }

    /**
     * {@link ConfigurationCustomizer} 的默认实现
     */
    public static class DefaultConfigurationCustomizer implements ConfigurationCustomizer {
    }

    /**
     * 获取 {@link ExceptionCodeManager}
     * @return {@link ExceptionCodeManager}
     */
    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    /**
     * 获取 {@link ConfigurationCustomizer}
     * @return {@link ConfigurationCustomizer}
     */
    public ConfigurationCustomizer getCustomizer() {
        return customizer;
    }


    private  <T> Message<T> creatingMessage(String code, String msg, T data) {
        return customizer.newMessage(code, msg, data);
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
        return customizer.newExecuteResult(
            state,
            getMessageWithKey(codeKey, msgKey, entity)
        );
    }

    public <T> QueryResult<T, ?> getQueryResult(Page<T> page, String codeKey, String msgKey) {
        return customizer.newQueryResult(
            getMessageWithKey(
                codeKey, msgKey, page
            )
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
    public <T> Message<T> getMessageWithKey(String codeKey, String msgKey, T data) {
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

}
