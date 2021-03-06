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

/**
 * 排除授权类型,  带有该注解的方法会忽略指定类型的参数注入。
 *
 * <br>
 *
 * 如只在<code>method</code>上使用, 需使用<code>@Scanned</code>标注该方法所在的类, 如果该类已使用<code>@Api</code>注解, 则需使用 {@link Api#authExclude()} 配置, 直接使用 {@link #value()} 不会生效。
 *
 * @author CofCool
 *
 * @see Api
 * @see Scanned
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scanned
public @interface DataAuthExclude {

    /**
     * 需要排除的字段, 需要和 {@link #mode()} 配合使用, 只有"always"模式时才会进行排除
     */
    String[] value() default { };

    /**
     * 是否需要检测
     * @return 默认不检测
     */
    ExcludeMode mode() default ExcludeMode.never;

    enum ExcludeMode {

        /**
         * 从不检测
         */
        never,

        /**
         * 始终检测
         */
        always
    }

}
