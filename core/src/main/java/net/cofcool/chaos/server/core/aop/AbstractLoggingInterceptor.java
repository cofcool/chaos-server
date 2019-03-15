package net.cofcool.chaos.server.core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.cofcool.chaos.server.common.util.WebUtils;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * 记录日志, 包括http请求与返回值等。被代理类需使用 {@link Scanned} 注解。
 *
 * @author CofCool
 */
public abstract class AbstractLoggingInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());


    protected abstract ObjectMapper getObjectMapper();


    protected StringBuilder appendAfterLog(StringBuilder sb, Object returnValue) {
        sb.append(";result=[");

        try {
            sb.append(getObjectMapper().writeValueAsString(returnValue));
        } catch (JsonProcessingException ignore) {

        }

        sb.append("]");

        return sb;
    }

    protected StringBuilder creatingLog(MethodInvocation invocation) {
        String requestPath = WebUtils.getRequestPath();
        String method = WebUtils.getRequest().getMethod();
        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        StringBuilder builder = new StringBuilder();

        builder.append("datetime=[").append(System.currentTimeMillis()).append("];");
        builder.append("url=[").append(requestPath).append("];");
        builder.append("headers=[").append(parseHeaders(WebUtils.getRequest())).append("];");
        builder.append("ip=[").append(WebUtils.getIp(WebUtils.getRequest())).append("];");
        builder.append("method=[").append(method).append("];");
        builder.append("class=[").append(targetName).append("];");
        builder.append("method=[").append(methodName).append("];");

        builder.append("parameter=[");
        int length = arguments.length;
        for (int i = 0; i < length; i++) {
            Object obj = arguments[i];
            if (obj instanceof ServletRequest || obj instanceof ServletResponse) {
                continue;
            }
            try {
                String value = getObjectMapper().writeValueAsString(obj);
                if (i == length - 1) {
                    builder.append(value);
                } else {
                    builder.append(value).append(" ");
                }
            } catch (Exception ignored) {

            }
        }
        builder.append("]");

        return builder;
    }

    private String parseHeaders(HttpServletRequest request) {
        Enumeration headerNames = request.getHeaderNames();

        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name= (String) headerNames.nextElement();
            headers.append(name).append("=").append(request.getHeader(name)).append(";");
        }

        return headers.toString();
    }

    @Override
    protected boolean doSupport(Method method, Class targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doAfter(MethodInvocation invocation, Object returnValue) throws Throwable {
        log.info("request: " + appendAfterLog(creatingLog(invocation), returnValue).toString());
    }

}