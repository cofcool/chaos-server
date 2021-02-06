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
import javax.annotation.Nullable;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * 参数校验切面, 处理含有 {@link BindingResult} 参数的方法, 通过拦截<b>Controller</b>实现, 被代理类需使用 {@link Scanned} 注解。
 *
 * @author zq
 * @author CofCool
 **/
public class ValidateInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());


    /**
     * 处理 BindingResult 携带的验证信息
     */
    protected void validate(MethodInvocation invocation) throws Exception {
        Object[] objects = invocation.getArguments();
        Method method = invocation.getMethod();
        if (objects.length == 0) {
            return;
        }

        for (Object object : objects) {
            if (object instanceof BindingResult) {
                String errMsg = getFirstErrorString((BindingResult) object);

                if (errMsg != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate message: {}", errMsg);
                    }

                    // 假定拦截对象为第一个参数
                    throw new MethodArgumentNotValidException(new MethodParameter(method, 0), (BindingResult) object);
                }
            }
        }
    }

    /**
     * 从 {@code BindingResult} 中读取验证信息, 如果没有, 则返回 {@literal null}
     * @param result BindingResult 实例
     * @return 验证信息
     */
    @Nullable
    public static String getFirstErrorString(BindingResult result) {
        if (result.hasErrors()) {
            String errMsg = "";
            ObjectError error = result.getAllErrors().iterator().next();
            if (error instanceof FieldError) {
                errMsg = ((FieldError) error).getField() + " ";
            }
            errMsg += error.getDefaultMessage();

            return errMsg;
        }

        return null;
    }

    @Override
    protected boolean doSupport(Method method, Class<?> targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doBefore(MethodInvocation invocation) throws Throwable {
        validate(invocation);
    }

}
