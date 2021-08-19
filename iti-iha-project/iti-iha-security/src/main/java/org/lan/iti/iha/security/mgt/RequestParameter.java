/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.iha.security.mgt;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.*;
import org.lan.iti.common.core.support.Mapped;
import org.lan.iti.iha.security.IhaSecurityConstants;
import org.lan.iti.iha.security.processor.ProcessorType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数封装，融合 HttpServletRequest 参数
 * <p>
 * 同时开放request返回
 *
 * @author NorthLan
 * @date 2021/7/31
 * @url https://blog.noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestParameter extends Mapped<RequestParameter> {
    private static final long serialVersionUID = 226639575409126193L;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<String, String> requestParameter;

    public RequestParameter() {
        this(null, null);
    }

    public RequestParameter(HttpServletRequest request) {
        this(request, null);
    }

    public RequestParameter(Map<? extends String, ?> m) {
        super(m);
    }

    public RequestParameter(RequestParameter other) {
        super(other);
        this.request = other.request;
        this.response = other.response;
        mergeRequest();
    }

    public RequestParameter(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
        mergeRequest();
    }

    public RequestParameter setRequest(HttpServletRequest request) {
        this.request = request;
        mergeRequest();
        return this;
    }

    private void mergeRequest() {
        if (this.request != null) {
            this.requestParameter = ServletUtil.getParamMap(this.request);
            this.putMap(this.requestParameter);
        }
    }

    public Map<String, Object> separateRequest() {
        Map<String, Object> result = new HashMap<>(this);
        for (String key : this.requestParameter.keySet()) {
            result.remove(key);
        }
        return result;
    }

    public Map<String, Object> separate(String... keys) {
        Map<String, Object> result = new HashMap<>(this);
        for (String key : keys) {
            result.remove(key);
        }
        return result;
    }

    public RequestParameter setProcessorType(String type) {
        put(ProcessorType.KEY, type);
        return this;
    }

    public String getProcessorType() {
        return getByKey(ProcessorType.KEY, ProcessorType.SIMPLE.getCode());
    }

    public RequestParameter setSessionId(String sessionId) {
        put(IhaSecurityConstants.SESSION_ID_CACHE_KEY, sessionId);
        return this;
    }

    public String getSessionId() {
        return getByKey(IhaSecurityConstants.SESSION_ID_CACHE_KEY);
    }
}
