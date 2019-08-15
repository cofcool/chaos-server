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
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExceptionCodeManager;
import net.cofcool.chaos.server.common.core.Message;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * 授权成功时调用
 *
 * @author CofCool
 */
public class JsonAuthenticationSuccessHandler extends AbstractAuthenticationConfigure implements AuthenticationSuccessHandler {


    public JsonAuthenticationSuccessHandler(
        ExceptionCodeManager exceptionCodeManager,
        MappingJackson2HttpMessageConverter messageConverter) {
        super(exceptionCodeManager, messageConverter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        getMessageConverter().write(
            Message.of(
                getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
                getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC),
                authentication.getPrincipal()
            ),
            MediaType.APPLICATION_JSON,
            new ServletServerHttpResponse(response)
        );
    }

}
