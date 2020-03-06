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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.Message;
import net.cofcool.chaos.server.common.security.exception.CaptchaErrorException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


/**
 * 授权失败时调用
 *
 * @author CofCool
 */
public class JsonAuthenticationFailureHandler extends AbstractAuthenticationConfigure implements AuthenticationFailureHandler {

    public JsonAuthenticationFailureHandler(
        ConfigurationSupport configuration,
        MappingJackson2HttpMessageConverter messageConverter) {
        super(configuration, messageConverter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        Message message;
        if (exception instanceof UsernameNotFoundException) {
            message = getMessage(ExceptionCodeDescriptor.NO_LOGIN, ExceptionCodeDescriptor.NO_LOGIN_DESC);
        } else if (exception instanceof BadCredentialsException) {
            message = getMessage(ExceptionCodeDescriptor.USER_PASSWORD_ERROR, ExceptionCodeDescriptor.USER_PASSWORD_ERROR_DESC);
        } else if (exception.getCause() instanceof CaptchaErrorException) {
            CaptchaErrorException cause = (CaptchaErrorException) exception.getCause();
            message = getConfiguration().getMessage(cause.getCode(), cause.getMessage(), null);
        } else {
            message = getMessage(ExceptionCodeDescriptor.AUTH_ERROR, exception.getMessage());
        }

        getMessageConverter().write(
            message,
            MediaType.APPLICATION_JSON,
            new ServletServerHttpResponse(response)
        );
    }

    private Message getMessage(String codeKey, String descKey) {
        return getConfiguration().getMessageWithKey(
            codeKey,
            descKey,
            null
        );
    }
}
