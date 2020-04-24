/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.social.security.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.social.connect.support.OAuth2ConnectionFactory;
import org.lan.iti.common.security.social.oauth2.AccessGrant;
import org.lan.iti.common.security.social.security.SocialAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth2基础鉴权服务
 *
 * @author NorthLan
 * @date 2020-04-01
 * @url https://noahlan.com
 */
@Slf4j
public class OAuth2AuthenticationService<S> extends AbstractSocialAuthenticationService<S> {
    @Setter
    private OAuth2ConnectionFactory<S> connectionFactory;

    @Getter
    @Setter
    private String defaultScope = "";

    public OAuth2AuthenticationService(OAuth2ConnectionFactory<S> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(getConnectionFactory(), "connectionFactory");
    }

    @Override
    public OAuth2ConnectionFactory<S> getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) {
        // oAuth 2.0 协议
        String code = request.getParameter("code");
        if (!StringUtils.hasText(code)) {
            //
            addCustomParameters();
            return null;
        } else if (StringUtils.hasText(code)) {
            OAuth2RestOperations auth2RestOperations = null;
            AccessGrant accessGrant = auth2RestOperations.getForObject("", AccessGrant.class);
            String domain = request.getParameter("domain");
            return new SocialAuthenticationToken(getConnectionFactory().createConnection(accessGrant, domain), null);
        } else {
            return null;
        }
    }

    private String generateState(OAuth2ConnectionFactory<?> connectionFactory, HttpServletRequest request) {
        final String state = request.getParameter("state");
        return (state != null) ? state : connectionFactory.generateState();
    }

    private void setScope(HttpServletRequest request) {

    }

    protected void addCustomParameters() {
    }
}
