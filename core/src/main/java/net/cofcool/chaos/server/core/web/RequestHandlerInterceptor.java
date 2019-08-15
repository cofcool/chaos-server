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

package net.cofcool.chaos.server.core.web;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 校验请求
 *
 * @author CofCool
 */
@Slf4j
public class RequestHandlerInterceptor implements HandlerInterceptor {

    private List<RequestChecker> requestCheckers;

    public RequestHandlerInterceptor(List<RequestChecker> requestCheckers) {
        Objects.requireNonNull(requestCheckers);

        AnnotationAwareOrderComparator.sort(requestCheckers);
        this.requestCheckers = requestCheckers;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        for (RequestChecker requestChecker : requestCheckers) {
            if (!requestChecker.check(request, response)) {
                if (log.isInfoEnabled()) {
                    log.info("the request({}) check result: false", requestChecker.getType());
                }

                return false;
            }
        }

        return true;
    }

}
