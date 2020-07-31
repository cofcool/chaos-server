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

package net.cofcool.chaos.server.core.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

/**
 * {@link HttpMessageConverter} 简单实现，可解析 {@code application/x-www-form-urlencoded}
 * <br>
 * 注意: 只支持读取, 不支持写入
 *
 * @author CofCool
 */
public class FormReaderHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[] { MediaType.APPLICATION_FORM_URLENCODED };


    public FormReaderHttpMessageConverter() {
        super(SUPPORTED_MEDIA_TYPES);
        setDefaultCharset(DEFAULT_CHARSET);
    }


    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        return readResolved(clazz, inputMessage);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        return readResolved(GenericTypeResolver.resolveType(type, contextClass), inputMessage);
    }

    private Object readResolved(Type resolvedType, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType != null && contentType.getCharset() != null ?
            contentType.getCharset() : getDefaultCharset());

        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        MutablePropertyValues result = new MutablePropertyValues();
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
            } else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
            }
        }

        if (resolvedType instanceof Class) {
            final Class<?> t = (Class<?>) resolvedType;
            Object resultInstance = BeanUtils.instantiateClass(t);
            // is OK?
            new DataBinder(resultInstance).bind(result);

            return resultInstance;
        }


        throw new HttpMessageNotReadableException("resolvedType is not a Class type", inputMessage);
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected void writeInternal(Object t, Type type, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        throw new HttpMessageNotWritableException("FormReaderHttpMessageConverter could not write request");
    }
}
