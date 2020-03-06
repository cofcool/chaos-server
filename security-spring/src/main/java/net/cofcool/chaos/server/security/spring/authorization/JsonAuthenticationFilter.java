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

package net.cofcool.chaos.server.security.spring.authorization;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.security.AbstractLogin;
import net.cofcool.chaos.server.common.security.AbstractLogin.DefaultLogin;
import net.cofcool.chaos.server.common.security.exception.CaptchaErrorException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

/**
 * 处理 Json 请求
 *
 * @author CofCool
 */
public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private  MappingJackson2HttpMessageConverter messageConverter;

    private Class<? extends AbstractLogin> LoginType;

    public JsonAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MappingJackson2HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public Class<? extends AbstractLogin> getLoginType() {
        if (LoginType == null) {
            LoginType = DefaultLogin.class;
        }

        return LoginType;
    }

    public void setLoginType(Class<? extends AbstractLogin> loginType) {
        LoginType = loginType;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        Object loginUser;
        try {
            /*
             * 可通过 SecurityJackson2Modules 获取 Jackson 相关配置
             * ObjectMapper mapper = new ObjectMapper();
             * ClassLoader loader = getClass().getClassLoader();
             * List<Module> modules = SecurityJackson2Modules.getModules(loader);
             * mapper.registerModules(modules);
             *
             * mapper.readValue()
             */
            loginUser = messageConverter.read(getLoginType(), new ServletServerHttpRequest(request));
        } catch (IOException | HttpMessageNotReadableException e) {
            throw new AuthenticationServiceException("cannot parse the json request", e);
        }

        AbstractLogin login = (AbstractLogin) loginUser;

        try {
            login.parseDevice(request);
        } catch (CaptchaErrorException e) {
            throw new AuthenticationServiceException("captcha error", e);
        }

        JsonAuthenticationToken authRequest = new JsonAuthenticationToken(login);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, JsonAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(getMessageConverter(), "messageConvert must be specified");
    }

}
