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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.cofcool.chaos.server.common.core.ConfigurationSupport;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * 授权成功时调用
 *
 * @author CofCool
 */
public class JsonAuthenticationSuccessHandler extends AbstractAuthenticationConfigure implements AuthenticationSuccessHandler {


    public JsonAuthenticationSuccessHandler(
        ConfigurationSupport configuration,
        HttpMessageConverters messageConverter) {
        super(configuration, messageConverter);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        writeMessageToResponse(request, response, authentication);
    }

}
