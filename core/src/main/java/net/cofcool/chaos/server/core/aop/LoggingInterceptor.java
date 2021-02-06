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

package net.cofcool.chaos.server.core.aop;

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
public class LoggingInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());


    protected StringBuilder appendAfterLog(StringBuilder sb, Object returnValue) {
        if (returnValue instanceof Throwable) {
            sb.append(";exception=[");
        } else {
            sb.append(";result=[");
        }
        sb.append(returnValue).append("]");

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

            String value = String.valueOf(obj);
            if (i == length - 1) {
                builder.append(value);
            } else {
                builder.append(value).append(" ");
            }
        }
        builder.append("]");

        return builder;
    }

    private String parseHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();

        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name= headerNames.nextElement();
            headers.append(name).append("=").append(request.getHeader(name)).append(";");
        }

        return headers.toString();
    }

    @Override
    protected boolean doSupport(Method method, Class<?> targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doAfter(MethodInvocation invocation, Object returnValue) throws Throwable {
        log.info("request: " + appendAfterLog(creatingLog(invocation), returnValue).toString());
    }

}