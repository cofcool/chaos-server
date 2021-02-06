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

import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.ServiceException;
import net.cofcool.chaos.server.common.security.Auth;
import net.cofcool.chaos.server.common.security.AuthService;
import net.cofcool.chaos.server.common.security.User;
import net.cofcool.chaos.server.common.security.UserRole;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.core.annotation.Api;
import net.cofcool.chaos.server.core.annotation.ApiVersion;
import net.cofcool.chaos.server.core.annotation.DataAuthExclude;
import net.cofcool.chaos.server.core.annotation.DataAuthExclude.ExcludeMode;
import net.cofcool.chaos.server.core.annotation.Scanned;
import net.cofcool.chaos.server.core.annotation.scanner.BeanResourceHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Controller;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 接口拦截, 包括 {@link User} 数据注入, 设备检查, 版本对比以及覆盖ID等, 通过拦截<b>Controller</b>实现, 被代理类需使用 {@link Scanned} 注解.
 * 注意: {@link AuthService} 实例名必须为 {@code authService}
 * <br>
 *
 * @see Scanned
 * @see Api
 * @see DataAuthExclude
 * @see ApiVersion
 *
 * @author CofCool
 */
@SuppressWarnings("rawtypes")
public class ApiProcessingInterceptor extends AbstractScannedMethodInterceptor implements
    ApplicationContextAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;
    private AuthService authService;

    private ConfigurationSupport configuration;

    protected ConfigurationSupport getConfiguration() {
        if (configuration == null) {
            configuration = ConfigurationSupport.getConfiguration();
        }
        return configuration;
    }

    public void setConfiguration(ConfigurationSupport configuration) {
        this.configuration = configuration;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

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

    private void checkApi(MethodInvocation invocation) {
        checkApi(invocation, getUser());
    }

    /**
     * 获取User
     * @return user
     */
    protected User getUser() {
        if (authService == null) {
            if (applicationContext != null && applicationContext.containsBean("authService")) {
                authService = applicationContext.getBean(AuthService.class);
            } else {
                return null;
            }
        }

        return authService.readCurrentUser();
    }


    protected void checkApi(MethodInvocation invocation, User user) {
        Api api = BeanResourceHolder.getAnnotation(invocation.getMethod(), Api.class, true);

        if (api != null) {
            checkDevice(api.device(), user);

            checkVersion(api.version());

            checkRole(api.userRoles(), user);

            setupAuth(user, invocation, api.authExclude());
        } else {
            ApiVersion apiVersion = BeanResourceHolder
                .getAnnotation(invocation.getMethod(), ApiVersion.class, true);
            checkVersion(apiVersion);

            DataAuthExclude authExclude = BeanResourceHolder
                .getAnnotation(invocation.getMethod(), DataAuthExclude.class, true);
            setupAuth(user, invocation, authExclude);
        }
    }

    protected void checkDevice(String[] devices, User user) {
        if (user != null && user.getDevice() != null && !user.getDevice().contained(devices)) {
            throwException(ExceptionCodeDescriptor.DENIAL_DEVICE);
        }
    }

    private void throwException(String code) {
        throw new ServiceException(
            getConfiguration().getExceptionDescription(code),
            getConfiguration().getExceptionCode(code)
        );
    }

    @SuppressWarnings("unchecked")
    protected void checkRole(String[] roles, User user) {
        BeanUtils.applyNonnull(user, data -> {
            Collection<UserRole> userRoles = data.getRoles();
            userRoles.forEach(userRole -> {
                if (!userRole.contains(roles)) {
                    throwException(ExceptionCodeDescriptor.DENIAL_OPERATING);
                }
            });

            return userRoles;
        });
    }

    protected void checkVersion(ApiVersion apiVersion) {
        if (apiVersion != null && (apiVersion.value() == ApiVersion.DENIAL_ALL || (apiVersion.value() != ApiVersion.ALLOW_ALL && compareApiVersion(apiVersion)))) {
            throwException(ExceptionCodeDescriptor.LOWEST_LEVEL_API);
        }
    }

    /**
     * 项目当前版本是否大于apiVersion定义的版本
     * @param apiVersion apiVersion
     */
    protected boolean compareApiVersion(ApiVersion apiVersion) {
        return version > apiVersion.value();
    }

    protected void setupAuth(User user, MethodInvocation invocation, DataAuthExclude authExclude) {
        Object[] args = invocation.getArguments();
        if (user != null && user.getDetail() != null) {
            Auth data = user.getDetail();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
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
                            setTheId(arg, data, ResolvableType.forMethodParameter(invocation.getMethod(), i).getGeneric(0), property);
                        } catch (Throwable e) {
                            log.debug(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    protected void setTheId(Object obj, Auth authData, ResolvableType type, String property) throws Throwable {
        Object value = BeanUtils.getPropertyDescriptor(authData.getClass(), property).getReadMethod().invoke(authData);
        // 检查值是否为 0, 如果是 0 的话忽略
        if (BeanUtils.checkNullOrZero(value).isPresent()) {
            if (obj instanceof Page) {
                copyIdsToCondition(
                    (Page) obj,
                    type == ResolvableType.NONE ? null : (Class<?>) type.getType(),
                    property,
                    value
                );
            } else {
                BeanUtils.getPropertyDescriptor(obj.getClass(), property).getWriteMethod().invoke(obj, value);
            }
        }
    }

    /**
     * 把 {@link User#getDetail()} 数据注入到 <code>condition</code> 对象中,
     * 如果 {@link Page#getCondition()} 为 <code>null</code> 则会创建该实例
     * 。 注意: 如果没有在方法参数中指定 {@link Page#getCondition()} 的类型，则不会创建
     * @param page 分页对象
     * @param clazz T
     * @param key 属性名
     * @param val 属性值
     */
    @SuppressWarnings("unchecked")
    protected void copyIdsToCondition(Page page, @Nullable Class<?> clazz, String key, Object val) throws Throwable {
        Object condition = page.getCondition();
        if (condition == null && clazz != null) {
            condition = org.springframework.beans.BeanUtils.instantiateClass(clazz);
            page.setCondition(condition);
        }

        if (condition == null) {
            return;
        }

        BeanUtils.getPropertyDescriptor(clazz, key).getWriteMethod().invoke(condition, val);
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
    protected boolean doSupport(Method method, Class<?> targetClass) {
        return BeanResourceHolder.findAnnotation(targetClass, Controller.class, false) != null;
    }

    @Override
    protected void doBefore(MethodInvocation invocation) {
        checkApi(invocation);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
