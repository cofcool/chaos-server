package net.cofcool.chaos.server.core.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.core.ExceptionLevel;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.core.config.DevelopmentMode;
import net.cofcool.chaos.server.core.config.WebApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 异常处理器，应用处于 {@link DevelopmentMode#DEV} 时优先级低于Spring默认异常解析器的，其它情况优先级最高
 *
 * @see DefaultHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
 * @see org.springframework.web.servlet.handler.HandlerExceptionResolverComposite
 *
 * @author CofCool
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    private HandlerExceptionResolver defaultExceptionResolver;

    protected static final ModelAndView EMPTY_MODEL_AND_VIEW = new ModelAndView();

    private ObjectMapper jacksonObjectMapper;

    public GlobalHandlerExceptionResolver() {
        if (!WebApplicationContext.getConfiguration().isDev()) {
            setOrder(Ordered.HIGHEST_PRECEDENCE);
        }
        defaultExceptionResolver = new DefaultHandlerExceptionResolver();
    }

    protected ObjectMapper getJacksonObjectMapper() {
        return jacksonObjectMapper;
    }

    protected void setJacksonObjectMapper(ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    protected HandlerExceptionResolver getDefaultExceptionResolver() {
        return defaultExceptionResolver;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                                              HttpServletResponse response, Object handler, Exception ex) {
        printExceptionLog(handler, ex);

        if (ex instanceof DuplicateKeyException) {
            return handleSqlIntegrityViolationException(response, ex);
        }
        else if (ex instanceof ServiceException) {
            return handleServiceException(response, ex);
        }
        else if (ex instanceof NullPointerException || ex instanceof IndexOutOfBoundsException || ex instanceof NoSuchElementException) {
            return handleNullException(response, ex);
        }
        else if (ex instanceof HttpMessageNotReadableException) {
            return handle4xxException(response, ex);
        } else if (ex instanceof UnsupportedOperationException) {
            return handle5xxException(response, ex);
        } else {
            return resolveOthersException(request, response, handler, ex);
        }
    }

    private void printExceptionLog(Object handler, Exception ex) {
        if (logger.isErrorEnabled() && ((!(ex instanceof ExceptionLevel)) || ((ExceptionLevel) ex).showable())) {
            OutputStream outputStream = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(outputStream));

            logger.error("exception log: " + outputStream.toString());
        }
    }

    private ModelAndView resolveOthersException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = this.defaultExceptionResolver.resolveException(request, response, handler, ex);
        if (modelAndView == null) {
            writeMessage(
                response,
                getMessage(
                    ExceptionCode.OPERATION_ERR,
                    WebApplicationContext.getConfiguration().isDev() ? ex.getMessage() : ExceptionCodeInfo.operatingError(),
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
            getMessage(ExceptionCode.OPERATION_ERR, ExceptionCodeInfo.dataError(), null)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle5xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getMessage(ExceptionCode.SERVER_ERR, ExceptionCodeInfo.serverError(), null)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handleSqlIntegrityViolationException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getMessage(ExceptionCode.DATA_EXISTS, ExceptionCodeInfo.dataExists(), null)
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handleServiceException(HttpServletResponse response, Exception ex) {
        ServiceException exception = (ServiceException) ex;
        writeMessage(
            response,
            getMessage(
                exception.getCode() == null ? ExceptionCode.OPERATION_ERR : exception.getCode(),
                exception.getMessage(),
            null
            )
        );

        return EMPTY_MODEL_AND_VIEW;
    }

    private ModelAndView handle4xxException(HttpServletResponse response, Exception ex) {
        writeMessage(
            response,
            getMessage(ExceptionCode.PARAM_ERROR, ExceptionCodeInfo.paramError(), null)
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

    protected Message getMessage(String code, String msg, Object data) {
        return Message.error(code, msg, data);
    }

}
