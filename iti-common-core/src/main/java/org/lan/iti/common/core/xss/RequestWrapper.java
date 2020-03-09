/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.core.xss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 请求转换器
 * 1. XSS过滤
 * 2. 构建可重复读取的body
 *
 * @author NorthLan
 * @date 2020-02-23
 * @url https://noahlan.com
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.body = getByteBody(request);
    }

    private byte[] getByteBody(HttpServletRequest request) {
        byte[] result = new byte[0];
        try {
            result = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            log.error("解析流中数据异常：", e);
        }
        return result;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return body.length == 0 ? null : new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null || values.length <= 0) {
            return null;
        }
        int len = values.length;
        String[] encodedValues = new String[len];
        for (int i = 0; i < len; ++i) {
            encodedValues[i] = HtmlUtils.htmlEscape(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }

    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return HtmlUtils.htmlEscape((String) value);
        }
        return value;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }

    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }
}
