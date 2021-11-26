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
import java.lang.reflect.UndeclaredThrowableException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionLevel;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.common.util.WebUtils;
import net.cofcool.chaos.server.core.aop.ValidateInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 异常处理器, 把异常信息转换为 {@link Message}, "Content-Type" 为 "JSON", 应用处于 {@link ConfigurationSupport#isDebug()} 时
 * 优先级低于Spring默认异常解析器的, 其它情况优先级最高,
 * 部分 {@linkplain Message 描述信息} 通过 {@link ConfigurationSupport#getMessage(String, Object)} 创建,
 * 如 {@link ServiceException} 等
 *
 * @see DefaultHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see org.springframework.web.servlet.handler.HandlerExceptionResolverComposite
 *
 * @author CofCool
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean {

    private final HandlerExceptionResolver defaultExceptionResolver;

    protected static final ModelAndView EMPTY_MODEL_AND_VIEW = new ModelAndView();

    private static Class<?> springDataAccessException;

    static {
        try {
            springDataAccessException = ClassUtils
                .resolveClassName("org.springframework.dao.DataAccessException", GlobalHandlerExceptionResolver.class.getClassLoader());
        } catch (Exception e) {
            springDataAccessException = null;
        }
    }

    private HttpMessageConverters httpMessageConverters;

    private ConfigurationSupport configuration;

    protected ConfigurationSupport getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
    }

    public GlobalHandlerExceptionResolver() {
        defaultExceptionResolver = new DefaultHandlerExceptionResolver();
    }

    protected HttpMessageConverters getHttpMessageConverters() {
        return httpMessageConverters;
    }

    public void setHttpMessageConverters(HttpMessageConverters httpMessageConverters) {
        this.httpMessageConverters = httpMessageConverters;
    }

    protected HandlerExceptionResolver getDefaultExceptionResolver() {
        return defaultExceptionResolver;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                                              HttpServletResponse response, Object handler, Exception ex) {
        Exception throwable = ex;
        if (ex instanceof UndeclaredThrowableException) {
            throwable = (Exception) ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
        }

        printExceptionLog(request, handler, throwable);

        if (throwable instanceof ServiceException) {
            return handleServiceException(response, (ServiceException) throwable);
        } else if (throwable instanceof NullPointerException || throwable instanceof IndexOutOfBoundsException || throwable instanceof NoSuchElementException) {
            return handleNullException(response, throwable);
        } else if (throwable instanceof HttpMessageNotReadableException) {
            return handle4xxException(response, throwable);
        } else if (throwable instanceof UnsupportedOperationException) {
            return handle5xxException(response, throwable);
        } else if (throwable instanceof BindException) {
            return resolveValidException(response, (BindException) throwable);
        } else if (springDataAccessException != null && springDataAccessException.isAssignableFrom(throwable.getClass())) {
            return handleSqlException(response, throwable);
        } else {
            return resolveOthersException(request, response, handler, throwable);
        }
    }

    private ModelAndView resolveValidException(HttpServletResponse response, BindException ex) {
        writeMessage(
            response,
            configuration.getMessage(
                ExceptionCodeDescriptor.PARAM_ERROR,
                ValidateInterceptor.getFirstErrorString(ex.getBindingResult()),
                null
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    protected void printExceptionLog(HttpServletRequest request, Object handler, Exception ex) {
        if (logger.isErrorEnabled() && ((!(ex instanceof ExceptionLevel)) || ((ExceptionLevel) ex).showable())) {
            logger.error("exception log: path=[" + WebUtils.getRealRequestPath(request) + "]", ex);
        }
    }

    protected ModelAndView resolveOthersException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = this.defaultExceptionResolver.resolveException(request, response, handler, ex);
        if (modelAndView == null) {
            writeMessage(
                response,
                configuration.getMessage(
                    ExceptionCodeDescriptor.OPERATION_ERR,
                    configuration.isDebug() ?
                        ex.getMessage()  : configuration.getExceptionDescription(ExceptionCodeDescriptor.OPERATION_ERR),
                    null
                )
            );
            modelAndView = EMPTY_MODEL_AND_VIEW;
        }

        return modelAndView;
    }

    private ModelAndView handleNullException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            configuration.getMessage(ExceptionCodeDescriptor.DATA_ERROR, null)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle5xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            configuration.getMessage(ExceptionCodeDescriptor.SERVER_ERR, null)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    /**
     * 处理 SQL 异常, 即 <code>org.springframework.dao.DataAccessException</code>
     * @param response HttpServletResponse
     * @param ex 异常
     * @return ModelAndView, 返回 {@link #EMPTY_MODEL_AND_VIEW}
     */
    protected ModelAndView handleSqlException(HttpServletResponse response, Exception ex) {
        writeMessage(
                response,
                configuration.getMessage(
                        ExceptionCodeDescriptor.DATA_ERROR,
                        configuration.isDebug() ?
                                ex.getMessage() : configuration.getExceptionDescription(ExceptionCodeDescriptor.DATA_ERROR),
                        null
                )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handleServiceException(HttpServletResponse response, ServiceException ex) {
        writeMessage(
                response,
                configuration.newMessage(
                        ex.getCode() == null ? configuration.getExceptionCode(ExceptionCodeDescriptor.OPERATION_ERR) : ex.getCode(),
                        ex.getMessage(),
                        null
                )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle4xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            configuration.getMessage(
                ExceptionCodeDescriptor.PARAM_ERROR,
                null
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    @SuppressWarnings("rawtypes")
    protected void writeMessage(HttpServletResponse response, Message message) {
        try {
            response.setCharacterEncoding("utf-8");
            WebUtils.writeObjToResponse(getHttpMessageConverters(), response, message, MediaType.APPLICATION_JSON);
        } catch (IOException | HttpMessageNotWritableException e) {
            if (logger.isErrorEnabled()) {
                logger.error("parse exception error: ", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(configuration, "configuration must be specified");
        Assert.notNull(httpMessageConverters, "httpMessageConverters must be specified");

        if (!configuration.isDebug()) {
            // 允许自定义更高优先级的异常处理器
            setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        }
    }
}
