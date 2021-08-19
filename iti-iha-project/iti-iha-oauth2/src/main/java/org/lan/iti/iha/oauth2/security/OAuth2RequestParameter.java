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

package org.lan.iti.iha.oauth2.security;

import cn.hutool.core.lang.Assert;
import org.lan.iti.iha.oauth2.ClientCertificate;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessorType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public class OAuth2RequestParameter extends RequestParameter {
    private static final long serialVersionUID = -4653455213727626913L;
    public static final String KEY_CONFIG = "$config.oauth2";

    public OAuth2RequestParameter(RequestParameter other) {
        super(other);
        initProcessorType();
    }

    public OAuth2RequestParameter(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        initProcessorType();
    }

    private void initProcessorType() {
        setProcessorType(ProcessorType.OAUTH2.getCode());
    }

    public OAuth2Config getConfig() {
        return getByKey(KEY_CONFIG);
    }

    public OAuth2RequestParameter setConfig(OAuth2Config config) {
        Assert.notNull(config, "config cannot null");
        put(KEY_CONFIG, config);
        return this;
    }

    public OAuth2RequestParameter setClient(ClientCertificate certificate) {
        Assert.notNull(certificate, "client cannot null");
        put(OAuth2ParameterNames.CLIENT_ID, certificate.getId());
        put(OAuth2ParameterNames.CLIENT_SECRET, certificate.getSecret());
        return this;
    }

    public String getClientId() {
        return getByKey(OAuth2ParameterNames.CLIENT_ID);
    }

    public String getClientSecret() {
        return getByKey(OAuth2ParameterNames.CLIENT_SECRET);
    }

    public String getResponseType() {
        return getByKey(OAuth2ParameterNames.RESPONSE_TYPE);
    }

    public String getCode() {
        return getByKey(OAuth2ParameterNames.CODE);
    }

    public String getUsername() {
        return getByKey(OAuth2ParameterNames.USERNAME);
    }

    public String getPassword() {
        return getByKey(OAuth2ParameterNames.PASSWORD);
    }

    public String getScope() {
        return getByKey(OAuth2ParameterNames.SCOPE);
    }

    public String getState() {
        return getByKey(OAuth2ParameterNames.STATE);
    }

    public String getAccessToken() {
        return getByKey(OAuth2ParameterNames.ACCESS_TOKEN);
    }

    public String getRefreshToken() {
        return getByKey(OAuth2ParameterNames.REFRESH_TOKEN);
    }

    public String getIdToken() {
        return getByKey(OAuth2ParameterNames.ID_TOKEN);
    }

    public String getTokenType() {
        return getByKey(OAuth2ParameterNames.TOKEN_TYPE);
    }

    public Long getExpiresIn() {
        return getByKey(OAuth2ParameterNames.EXPIRES_IN);
    }

    public Long getRefreshTokenExpiresIn() {
        return getByKey(OAuth2ParameterNames.REFRESH_TOKEN_EXPIRES_IN);
    }

    public Long getIdTokenExpiresIn() {
        return getByKey(OAuth2ParameterNames.ID_TOKEN_EXPIRES_IN);
    }
}
