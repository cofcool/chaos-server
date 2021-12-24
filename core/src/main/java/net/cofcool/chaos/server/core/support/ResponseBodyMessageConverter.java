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

package net.cofcool.chaos.server.core.support;

import java.io.IOException;
import java.lang.reflect.Type;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Message.MessageWrapped;
import net.cofcool.chaos.server.common.core.Result;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 处理响应数据(JSON), {@link Result} 等
 *
 * @author CofCool
 */
public class ResponseBodyMessageConverter extends MappingJackson2HttpMessageConverter {

    private ConfigurationSupport configuration;

    protected ConfigurationSupport getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        if (object instanceof Result) {
            object = ((Result) object).result();
        } else if (object instanceof Number || object instanceof String){
            object = configuration.getMessage(
                ExceptionCodeDescriptor.SERVER_OK,
                object
            );
        } else if (object instanceof Result.ResultState) {
            ResultState state = (ResultState) object;
            if (state == ResultState.SUCCESSFUL) {
                object = configuration.getMessage(
                    ExceptionCodeDescriptor.SERVER_OK,
                    null
                );
            } else {
                object = configuration.getMessage(
                    ExceptionCodeDescriptor.OPERATION_ERR,
                    null
                );
            }
        } else if (AnnotatedElementUtils.hasAnnotation(object.getClass(), MessageWrapped.class)) {
            object = configuration.getMessage(ExceptionCodeDescriptor.SERVER_OK, object);
        }

        super.writeInternal(object, type, outputMessage);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (Result.class.isAssignableFrom(clazz)) {
            return true;
        }

        return super.canWrite(clazz, mediaType);
    }

}
