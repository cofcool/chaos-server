package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import net.cofcool.chaos.server.common.core.ExceptionCode;
import net.cofcool.chaos.server.common.core.ExceptionLevel;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import net.cofcool.chaos.server.core.support.ExceptionCodeInfo;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * 参数校验切面，通过拦截<b>Controller</b>实现，被代理类需使用 {@link Scanned} 注解。
 *
 * @author zq
 * @author CofCool
 **/
public abstract class AbstractValidateInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final ExecutableValidator validator = factory.getValidator().forExecutables();
    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    protected void validate(MethodInvocation invocation) throws Throwable {
        Object[] objects = invocation.getArguments();
        Method method = invocation.getMethod();
        if (objects.length == 0) {
            return;
        }

        for (Object object : objects) {
            if (object instanceof BeanPropertyBindingResult) {
                BeanPropertyBindingResult result = (BeanPropertyBindingResult) object;
                if (result.hasErrors()) {
                    List<ObjectError> list = result.getAllErrors();

                    if (!list.isEmpty()) {
                        ObjectError error = list.get(0);
                        String field = "";
                        if (error instanceof FieldError) {
                            field = ((FieldError) error).getField();
                        }
                        if (log.isInfoEnabled()) {
                            log.info("validate message: {} - {}", field, error.getDefaultMessage());
                        }

                        throwErrorException(
                            method,
                            Message.error(
                                ExceptionCode.PARAM_ERROR,
                                error.getDefaultMessage() != null ? error.getDefaultMessage() : ExceptionCodeInfo
                                    .paramError(),
                                null
                            )
                        );
                    }
                }
            }
        }

        Object target = invocation.getThis();
        Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, objects);

        String paramName = "";
        if (!validResult.isEmpty()) {
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

            for (ConstraintViolation<Object> constraintViolation : validResult) {
                PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
                int paramIndex = pathImpl.getLeafNode().getParameterIndex();
                paramName = parameterNames[paramIndex];
            }

            if (log.isInfoEnabled()) {
                log.info("{} validate message: {}", paramName,validResult.iterator().next().getMessage());
            }

            throwErrorException(
                method,
                Message.error(
                    ExceptionCode.PARAM_ERROR,
                    validResult.iterator().next().getMessage(),
                    null
                )
            );
        }
    }

    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object[] params) {
        return validator.validateParameters(obj, method, params);
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
