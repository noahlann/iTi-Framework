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

package org.lan.iti.iha.oidc.security;

import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oidc.OidcConfig;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessorType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
public class OidcRequestParameter extends OAuth2RequestParameter {
    private static final long serialVersionUID = 1128492042840491394L;
    public static final String KEY_CONFIG = "$config.oidc";

    public OidcRequestParameter(RequestParameter other) {
        super(other);
        initProcessorType();
    }

    public OidcRequestParameter(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        initProcessorType();
    }

    private void initProcessorType() {
        setProcessorType(ProcessorType.OIDC.getCode());
    }

    @Override
    public OidcConfig getConfig() {
        return getByKey(KEY_CONFIG);
    }

    public OidcRequestParameter setConfig(OidcConfig config) {
        put(KEY_CONFIG, config);
        return this;
    }
}
