package net.cofcool.chaos.server.core.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserRole;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.core.annotation.ApiVersion;
import net.cofcool.chaos.server.core.annotation.DataAuthExclude;
import net.cofcool.chaos.server.core.annotation.DataAuthExclude.ExcludeMode;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import net.cofcool.chaos.server.core.config.ChaosProperties;
import net.cofcool.chaos.server.core.support.ExceptionCodeInfo;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * 接口拦截, 包括 {@link User} 数据注入, 设备检查, 版本对比以及覆盖ID等, 通过拦截<b>Controller</b>实现,  被代理类需使用 {@link Scanned} 注解。
 * <br>
 *
 * @see Scanned
 * @see Api
 * @see DataAuthExclude
 * @see ApiVersion
 *
 * @author CofCool
 */
public abstract class AbstractApiInterceptor extends AbstractScannedMethodInterceptor {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 注入到参数的数据的key
     */
    private String[] definedCheckedKeys;

    /**
     * 项目版本号
     */
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String[] getDefinedCheckedKeys() {
        return definedCheckedKeys;
    }

    public void setDefinedCheckedKeys(String[] definedCheckedKeys) {
        this.definedCheckedKeys = definedCheckedKeys;
    }

    @Autowired
    public void setupProperties(ChaosProperties properties) {
        definedCheckedKeys = StringUtils
            .delimitedListToStringArray(properties.getAuth().getCheckedKeys(), ",");
        version = properties.getDevelopment().getVersion();
    }

    private void checkApi(MethodInvocation invocation) {
        checkApi(invocation, getUser());
    }

    /**
     * 获取User
     * @return user
     */
    protected abstract User getUser();


    protected void checkApi(MethodInvocation invocation, User user) {
        Api api = BeanResourceHolder.getAnnotation(invocation.getMethod(), Api.class, true);

        if (api != null) {
            checkDevice(api.device(), user);

            checkVersion(api.version());

            checkRole(api.userRoles(), user);

            setupAuth(user, invocation.getArguments(), api.authExclude());
        } else {
            ApiVersion apiVersion = BeanResourceHolder
                .getAnnotation(invocation.getMethod(), ApiVersion.class, true);
            checkVersion(apiVersion);

            DataAuthExclude authExclude = BeanResourceHolder
                .getAnnotation(invocation.getMethod(), DataAuthExclude.class, true);
            setupAuth(user, invocation.getArguments(), authExclude);
        }
    }

    protected void checkDevice(String[] devices, User user) {
        if (user != null && user.getDevice() != null && !user.getDevice().contained(devices)) {
            throw new ServiceException(ExceptionCodeInfo.denialDevice());
        }
    }

    @SuppressWarnings("unchecked")
    protected void checkRole(int[] roles, User user) {
        BeanUtils.applyNonnull(user, data -> {
            Collection<UserRole> userRoles = data.getRoles();
            userRoles.forEach(userRole -> {
                if (!userRole.contains(roles)) {
                    throw new ServiceException(ExceptionCodeInfo.denialOperating());
                }
            });

            return userRoles;
        });
    }

    protected void checkVersion(ApiVersion apiVersion) {
        if (apiVersion != null && (apiVersion.value() == ApiVersion.DENIAL_ALL || (apiVersion.value() != ApiVersion.ALLOW_ALL && compareApiVersion(apiVersion)))) {
            throw new ServiceException(ExceptionCodeInfo.lowApiVersion());
        }
    }

    /**
     * 项目当前版本是否大于apiVersion定义的版本
     * @param apiVersion apiVersion
     */
    protected boolean compareApiVersion(ApiVersion apiVersion) {
        return version > apiVersion.value();
    }

    protected void setupAuth(User user, Object[] args, DataAuthExclude authExclude) {
        if (user != null && user.getDetail() != null) {
            Auth data = user.getDetail();
            for (Object arg : args) {
                // inject user
                if (arg instanceof User) {
                    user.cloneUser((User) arg);
                    continue;
                }

                // inject user properties
                if (arg != null) {
                    for (String property : getDefinedCheckedKeys()) {
                        if (checkExclude(authExclude, property)) {
                            continue;
                        }

                        try {
                            setTheId(arg, data, property);
                        } catch (Exception e) {
                            log.debug(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    protected void setTheId(Object obj, Auth authData, String property) throws IllegalAccessException, NullPointerException, InvocationTargetException {
        Object value = BeanUtils.getPropertyDescriptor(authData.getClass(), property).getReadMethod().invoke(authData);
        if (BeanUtils.checkNullOrZero(value).isPresent()) {
            if (obj instanceof Page) {
                ((Page) obj).putMappedValue(property, value);
            } else {
                BeanUtils.getPropertyDescriptor(obj.getClass(), property).getWriteMethod().invoke(obj, value);
            }
        }
    }

    private boolean checkExclude(DataAuthExclude originExclude, String excludeProperty) {
        if (originExclude != null && originExclude.mode().equals(ExcludeMode.always)) {
            for (String val : originExclude.value()) {
                if (val.equals(excludeProperty)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean doSupport(Method method, Class targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doBefore(MethodInvocation invocation) {
        checkApi(invocation);
    }

}
