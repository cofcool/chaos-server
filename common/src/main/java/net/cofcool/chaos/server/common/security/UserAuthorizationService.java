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

package net.cofcool.chaos.server.common.security;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.cofcool.chaos.server.common.core.Message;

/**
 * 授权相关方法定义, 用户需实现该类处理相关逻辑
 * <br>
 * <b>注意</b> : 由于本实例创建较早, 会导致关联组件提早创建, 可能会导致关联组件的事务失效, 谨慎操作, 避免插入修改等操作。
 *
 * @author CofCool
 */
public interface UserAuthorizationService<T extends Auth, ID extends Serializable> {

    /**
     * 查询登陆用户
     * @param loginUser 登陆参数
     * @return 查询结果
     */
    User<T, ID> queryUser(AbstractLogin loginUser);

    /**
     * 检查用户, 登陆时调用
     * @param currentUser 当前用户
     * @return {@link Message}
     */
    Message<Boolean> checkUser(User<T, ID> currentUser);

    /**
     * 配置登陆时的用户数据
     * @param currentUser 当前用户
     */
    void setupUserData(User<T, ID> currentUser);

    /**
     * 检查权限
     * @param servletRequest 请求
     * @param servletResponse 响应
     * @param authenticationInfo 授权信息
     * @param requestPath 请求路径
     *
     * @throws net.cofcool.chaos.server.common.security.exception.AuthorizationException 抛出相关权限异常
     */
    void checkPermission(ServletRequest servletRequest, ServletResponse servletResponse, Object authenticationInfo, String requestPath);

}
