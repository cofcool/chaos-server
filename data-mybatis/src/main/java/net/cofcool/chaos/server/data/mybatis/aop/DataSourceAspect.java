package net.cofcool.chaos.server.data.mybatis.aop;

import java.lang.reflect.Method;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.core.datasource.DataSourceType;
import net.cofcool.chaos.server.core.datasource.DynamicDataSourceHolder;
import net.cofcool.chaos.server.data.mybatis.transaction.DynamicManagedTransaction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换切面
 **/
public class DataSourceAspect {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected void detectDataSource(JoinPoint point) throws Throwable {
        Class<?> target = point.getTarget().getClass();
        MethodSignature signature = (MethodSignature) point.getSignature();

        if (!resolveDataSource(target, signature.getMethod())) {
            for (Class<?> clazz : target.getInterfaces()) {
                if (resolveDataSource(clazz, signature.getMethod())) {
                    return;
                }
            }
        }
    }

    protected void clearDataSource(Object returnValue) {
        setupDataSource(null);
    }

    /**
     * 提取目标对象方法注解和类型注解中的数据源标识
     */
    private boolean resolveDataSource(Class<?> clazz, Method method) {
        DataSourceType dataSource = null;
        try {
            Class<?>[] types = method.getParameterTypes();
            Method m = clazz.getMethod(method.getName(), types);

            dataSource = BeanUtils.chooseNotNullData(
                    clazz.getAnnotation(DataSourceType.class),
                    m.getAnnotation(DataSourceType.class)
            );

            setupDataSource(dataSource);
        } catch (Exception e) {
            log.error("resolve datasource error: {}", e);
        }

        return dataSource != null;
    }

    private void setupDataSource(DataSourceType source) {
        if (source != null) {
            DynamicManagedTransaction.cancelConnection();
            DynamicDataSourceHolder.setDataSource(source.value());
            log.info(source.value());
        } else {
            DynamicManagedTransaction.resetConnection();
            DynamicDataSourceHolder.removeDataSource();
        }
    }

}
