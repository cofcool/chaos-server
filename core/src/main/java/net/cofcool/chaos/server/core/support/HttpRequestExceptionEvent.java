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

package net.cofcool.chaos.server.core.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEvent;

/**
 * 当 {@link GlobalHandlerExceptionResolver} 捕获异常后发送事件
 * @author CofCool
 */
public class HttpRequestExceptionEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5496071994874706814L;

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpRequestExceptionEvent(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        super(ex);
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public Exception getSource() {
        return (Exception) super.getSource();
    }
}
