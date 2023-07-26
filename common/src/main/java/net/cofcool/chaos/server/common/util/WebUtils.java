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

package net.cofcool.chaos.server.common.util;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import javax.annotation.Nullable;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * web工具类
 */
public final class WebUtils {

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request.getSession();
    }

    /**
     * 获取请求uri
     */
    public static String getRequestUri() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getRequestURI();
    }

    /**
     * 获取请求路径
     */
    @SuppressWarnings("unchecked")
    public static String getRequestPath() {
        HttpServletRequest httpServletRequest = getRequest();
        StringBuilder builder = new StringBuilder();
        String scheme = httpServletRequest.getScheme();
        String serverName = httpServletRequest.getServerName();
        int serverPort = httpServletRequest.getServerPort();
        String requestURI = httpServletRequest.getRequestURI();
        builder.append(scheme);
        builder.append("://");
        builder.append(serverName);
        builder.append(":");
        builder.append(serverPort);
        builder.append(requestURI);

        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        boolean hasMoreElements = parameterNames.hasMoreElements();
        if (hasMoreElements) {
            builder.append("?");
        }
        while (parameterNames.hasMoreElements()) {
            String element = parameterNames.nextElement();
            builder.append(element);
            builder.append("=");
            builder.append(httpServletRequest.getParameter(element));
            builder.append("&");
        }
        String str = builder.toString();
        if (hasMoreElements) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isNullOrEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后,第一个ip为真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isNullOrEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String getRealRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (StringUtils.length(pathInfo) > 0) {
            servletPath = servletPath + pathInfo;
        }

        return servletPath;
    }

    public static WebApplicationContext getWebContext() {
        return (WebApplicationContext) getRequest().getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    /**
     * 设置CORS响应头
     * @param servletResponse 响应
     * @return ServletResponse
     */
    public static ServletResponse setupCorsHeader(HttpServletResponse servletResponse) {
        return setupCorsHeader(servletResponse, null);
    }

    /**
     * 设置CORS响应头
     * @param servletResponse 响应
     * @param allowedDomain 允许跨域请求的域名, 默认为 <b>*<b/>
     * @return ServletResponse
     */
    public static ServletResponse setupCorsHeader(HttpServletResponse servletResponse, @Nullable String allowedDomain) {
        // js client setting
        //
        // credentials:
        //  include: will set cookies
        //  omit: ignore cookie
        //  same-origin
        //
        // example:
        //
        //        fetch(
        //            new Request(url),
        //            {
        //                method: 'POST',
        //                mode: 'cors',
        //                credentials: 'include',
        //                headers: {
        //                    'Content-Type': 'application/json'
        //                },
        //                body: data
        //            }
        //         ).then(response => {}).then(data => {}).catch(error => {})


        allowedDomain = allowedDomain != null ? allowedDomain : "*";

        servletResponse.setHeader("Access-Control-Allow-Origin", allowedDomain);
        servletResponse.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, Authorization");
        servletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, HEAD, OPTIONS");
        servletResponse.setHeader("Access-Control-ALLOW-Credentials", "true");
        servletResponse.setHeader("Access-Control-Max-Age", "43200");

        return servletResponse;
    }

    /**
     * 解析数据并写入 {@code response}, 参考 {@link HttpMessageConverter#write(Object, MediaType, HttpOutputMessage)}
     *
     * @param messageConverters messageConverters
     * @param response {@code response}
     * @param result 数据对象
     *
     * @throws IOException 发生 IO 错误
     * @throws HttpMessageNotWritableException 解析失败
     * @throws IllegalStateException 未找到合适的 {@link HttpMessageConverter}
     */
    @SuppressWarnings("unchecked, rawtypes")
    public static void writeObjToResponse(HttpMessageConverters messageConverters, HttpServletResponse response, Object result, MediaType mediaType) throws IOException {
        for (HttpMessageConverter convert : messageConverters.getConverters()) {
            if (convert.canWrite(result.getClass(), mediaType)) {
                convert.write(
                    result,
                    mediaType,
                    new ServletServerHttpResponse(response)
                );

                return;
            }
        }

        throw new IllegalStateException("no HttpMessageConverter be found for " + MediaType.APPLICATION_JSON.toString());
    }

    /**
     * 解析 {@code request} 中的数据
     *
     * @param messageConverters messageConverters
     * @param request {@code request}
     * @param type 数据类型
     * @param <T> 数据类型
     * @return 解析后的对象
     *
     * @throws IOException 发生 IO 错误
     * @throws HttpMessageNotReadableException 解析失败
     * @throws IllegalStateException 未找到合适的 {@link HttpMessageConverter}
     */
    @SuppressWarnings("unchecked, rawtypes")
    public static <T> T readObjFromRequest(HttpMessageConverters messageConverters, HttpServletRequest request, Class<? extends T> type) throws IOException {
        MediaType mediaType = MediaType.parseMediaType(request.getContentType());
        for (HttpMessageConverter convert : messageConverters.getConverters()) {
            if (convert.canRead(type, mediaType)) {
                return (T) convert.read(
                    type,
                    new ServletServerHttpRequest(request)
                );
            }
        }

        throw new IllegalStateException("no HttpMessageConverter be found for " + mediaType.toString());
    }

}
