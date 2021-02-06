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

package net.cofcool.chaos.server.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.cofcool.chaos.server.common.security.Device;
import net.cofcool.chaos.server.common.security.UserRole;

/**
 * 接口属性定义, 包括版本号, 支持设备, 数据过滤, 描述等
 *
 * <br>
 *
 * 如只在<code>method</code>上使用, 需使用<code>@Scanned</code>标注该方法所在的类
 *
 * @see ApiVersion
 * @see DataAuthExclude
 * @see Device
 * @see Scanned
 *
 * @author CofCool
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scanned
public @interface Api {

    /**
     * 支持设备
     */
    String[] device() default Device.ALLOW_ALL;

    /**
     * 支持版本号
     * @return {@link ApiVersion}
     */
    ApiVersion version() default @ApiVersion;

    /**
     * 数据过滤
     * @return {@link DataAuthExclude}
     */
    DataAuthExclude authExclude() default @DataAuthExclude;

    /**
     * 描述信息
     */
    String desc() default "";

    /**
     * 适用用户角色
     *
     * @see UserRole
     */
    String[] userRoles() default { };

}
