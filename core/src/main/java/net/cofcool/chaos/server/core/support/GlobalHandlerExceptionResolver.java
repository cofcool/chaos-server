package net.cofcool.chaos.server.core.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.ExceptionLevel;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.common.util.WebUtils;
import net.cofcool.chaos.server.core.aop.ValidateInterceptor;
import net.cofcool.chaos.server.core.config.DevelopmentMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 异常处理器, 应用处于 {@link DevelopmentMode#DEV} 时优先级低于Spring默认异常解析器的, 其它情况优先级最高
 *
 * @see DefaultHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see org.springframework.web.servlet.handler.HandlerExceptionResolverComposite
 *
 * @author CofCool
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver implements InitializingBean {

    private HandlerExceptionResolver defaultExceptionResolver;

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

    private ObjectMapper jacksonObjectMapper;

    private ExceptionCodeManager exceptionCodeManager;

    public ExceptionCodeManager getExceptionCodeManager() {
        return exceptionCodeManager;
    }

    public void setExceptionCodeManager(ExceptionCodeManager exceptionCodeManager) {
        this.exceptionCodeManager = exceptionCodeManager;
    }

    private DevelopmentMode developmentMode;

    public DevelopmentMode getDevelopmentMode() {
        return developmentMode;
    }

    /**
     * 该设置需在 Spring 创建 Bean 结束前设置，可通过 {@link #afterPropertiesSet()} 或构造方法
     */
    public void setDevelopmentMode(DevelopmentMode developmentMode) {
        this.developmentMode = developmentMode;

        if (!developmentMode.isDebugMode()) {
            setOrder(Ordered.HIGHEST_PRECEDENCE);
        }
    }

    public GlobalHandlerExceptionResolver() {
        defaultExceptionResolver = new DefaultHandlerExceptionResolver();
    }

    public ObjectMapper getJacksonObjectMapper() {
        return jacksonObjectMapper;
    }

    public void setJacksonObjectMapper(ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    protected HandlerExceptionResolver getDefaultExceptionResolver() {
        return defaultExceptionResolver;
    }



    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                                              HttpServletResponse response, Object handler, Exception ex) {
        printExceptionLog(request, handler, ex);

        Exception throwable = ex;
        if (ex instanceof UndeclaredThrowableException) {
            throwable = (Exception) ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
        }

        if (throwable instanceof ServiceException) {
            return handleServiceException(response, (ServiceException) throwable);
        } else if (throwable instanceof NullPointerException || throwable instanceof IndexOutOfBoundsException || throwable instanceof NoSuchElementException) {
            return handleNullException(response, throwable);
        } else if (throwable instanceof HttpMessageNotReadableException) {
            return handle4xxException(response, throwable);
        } else if (throwable instanceof UnsupportedOperationException) {
            return handle5xxException(response, throwable);
        } else if (throwable instanceof MethodArgumentNotValidException) {
            return resolveValidException(response, (MethodArgumentNotValidException) throwable);
        } else if (springDataAccessException != null && springDataAccessException.isAssignableFrom(throwable.getClass())) {
            return handleSqlException(response, throwable);
        } else {
            return resolveOthersException(request, response, handler, ex);
        }
    }

    private ModelAndView resolveValidException(HttpServletResponse response, MethodArgumentNotValidException ex) {
        writeMessage(
            response,
            getMessage(
                ExceptionCodeDescriptor.PARAM_NULL,
                ValidateInterceptor.getFirstErrorString(ex.getBindingResult())
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    protected void printExceptionLog(HttpServletRequest request, Object handler, Exception ex) {
        if (logger.isErrorEnabled() && ((!(ex instanceof ExceptionLevel)) || ((ExceptionLevel) ex).showable())) {
            OutputStream outputStream = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(outputStream));

            logger.error("exception log: path=[" + WebUtils.getRealRequestPath(request) + "];exception=[" + outputStream.toString() + "]");
        }
    }

    protected ModelAndView resolveOthersException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = this.defaultExceptionResolver.resolveException(request, response, handler, ex);
        if (modelAndView == null) {
            writeMessage(
                response,
                getMessage(
                    exceptionCodeManager.getCode(ExceptionCodeDescriptor.OPERATION_ERR),
                    developmentMode.isDebugMode() ? ex.getMessage() : exceptionCodeManager.getDescription(
                        ExceptionCodeDescriptor.OPERATION_ERR_DESC)
                )
            );
            modelAndView = EMPTY_MODEL_AND_VIEW;
        }

        return modelAndView;
    }

    private ModelAndView handleNullException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getExceptionMessage(ExceptionCodeDescriptor.DATA_ERROR, ExceptionCodeDescriptor.DATA_ERROR_DESC)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle5xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getExceptionMessage(ExceptionCodeDescriptor.SERVER_ERR, ExceptionCodeDescriptor.SERVER_ERR_DESC)
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
            getMessage(
                exceptionCodeManager.getCode(ExceptionCodeDescriptor.DATA_ERROR),
                developmentMode.isDebugMode() ? ex.getMessage() : exceptionCodeManager.getDescription(
                    ExceptionCodeDescriptor.DATA_ERROR_DESC)
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handleServiceException(HttpServletResponse response, ServiceException ex) {
        writeMessage(
            response,
            getMessage(
                ex.getCode() == null ? exceptionCodeManager.getCode(ExceptionCodeDescriptor.OPERATION_ERR) : ex.getCode(),
                ex.getMessage()
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle4xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getExceptionMessage(ExceptionCodeDescriptor.PARAM_ERROR, ExceptionCodeDescriptor.PARAM_ERROR_DESC)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    protected void writeMessage(HttpServletResponse response, Message message) {
        try {
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().append(jacksonObjectMapper.writeValueAsString(message));
            response.flushBuffer();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("parse exception error: ",e);
            }
        }
    }

    protected Message getExceptionMessage(String code, String type) {
        return Message.of(
            exceptionCodeManager.getCode(code),
            exceptionCodeManager.getDescription(type)
        );
    }

    protected Message getMessage(String code, String message) {
        return Message.of(
            code,
            message
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getDefaultExceptionResolver() == null) {
            setDevelopmentMode(DevelopmentMode.RELEASE);
        }
    }
}
