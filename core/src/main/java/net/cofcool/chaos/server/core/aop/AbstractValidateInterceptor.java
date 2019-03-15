package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.core.ExceptionLevel;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * 参数校验切面, 通过拦截<b>Controller</b>实现, 被代理类需使用 {@link Scanned} 注解。
 *
 * @author zq
 * @author CofCool
 **/
public abstract class AbstractValidateInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());


    /**
     * 处理 BindingResult 携带的验证信息
     */
    protected void validate(MethodInvocation invocation) {
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

                    throwErrorException(
                        method,
                        Message.error(
                            ExceptionCode.PARAM_ERROR,
                            errMsg,
                            null
                        )
                    );
                }
            }
        }
    }

    /**
     * 从 <code>BindingResult</code> 中读取验证信息, 如果没有, 则返回 "NULL"
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

    protected void throwErrorException(Method method, Message message) {
        throw new ServiceException(message.getMessage(), message.getCode(), ExceptionLevel.LOWEST_LEVEL);
    }

    @Override
    protected boolean doSupport(Method method, Class targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doBefore(MethodInvocation invocation) throws Throwable {
        validate(invocation);
    }

}
