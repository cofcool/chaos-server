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

package net.cofcool.chaos.server.core.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.core.support.ResponseBodyMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * @author CofCool
 */
@Slf4j
public class MultiArgumentResolver implements HandlerMethodArgumentResolver, InitializingBean {

    private HandlerMethodArgumentResolver jsonResolver;

    private HandlerMethodArgumentResolver requestParamResolver = new ServletModelAttributeMethodProcessor(false);

    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return hasMultiRequestTypesAnnotation(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);


        if (checkGetRequest(parameter, request)) {
            return requestParamResolver
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }

        try {
            return getJsonResolver().resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        } catch (NullPointerException e) {
            log.info("resolve json error, try parse by get processor: ", e);
            return requestParamResolver
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
    }

    /**
     * post请求时body为空的话抛出异常
     */
    private boolean checkBodyForJsonContent(HttpServletRequest request) throws IOException {
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);

        return HttpMethod.POST.matches(request.getMethod()) && inputMessage.getBody().available() >= 0;
    }

    private boolean checkGetRequest(MethodParameter parameter, HttpServletRequest request) {
        return
                HttpMethod.GET.matches(request.getMethod()) && hasMultiRequestTypesAnnotation(parameter);
    }

    private boolean hasMultiRequestTypesAnnotation(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MultiRequestTypes.class);
    }

    @SuppressWarnings("unchecked")
    private HandlerMethodArgumentResolver getJsonResolver() {
        if (this.jsonResolver == null) {
            List resolvers = new ArrayList<>();
            resolvers.add(getJackson2HttpMessageConverter());

            jsonResolver = new RequestResponseBodyMethodProcessor(resolvers);
        }

        return jsonResolver;
    }

    AbstractJackson2HttpMessageConverter getJackson2HttpMessageConverter() {
        AbstractJackson2HttpMessageConverter converter = new ResponseBodyMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(mediaTypes);
        converter.setPrettyPrint(true);

        converter.setObjectMapper(getObjectMapper());

        return converter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getObjectMapper(), "objectMapper - this argument is required; it must not be null");
    }

}
