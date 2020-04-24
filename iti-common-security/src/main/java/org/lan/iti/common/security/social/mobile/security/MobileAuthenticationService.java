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

package org.lan.iti.common.security.social.mobile.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.social.connect.support.NopConnectionFactory;
import org.lan.iti.common.security.social.nop.NopGrant;
import org.lan.iti.common.security.social.security.SocialAuthenticationToken;
import org.lan.iti.common.security.social.security.provider.AbstractSocialAuthenticationService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 手机号 鉴权服务
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
@Slf4j
public class MobileAuthenticationService<S> extends AbstractSocialAuthenticationService<S> {
    @Setter
    private NopConnectionFactory<S> connectionFactory;

    public MobileAuthenticationService(NopConnectionFactory<S> connectionFactory) {
        setConnectionFactory(connectionFactory);
    }

    @Override
    public NopConnectionFactory<S> getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) {
        // TODO 验证 code domain
        return new SocialAuthenticationToken(
                getConnectionFactory().createConnection(wrapNopGrant(request), request.getParameter("domain")),
                null);
    }

    private NopGrant wrapNopGrant(HttpServletRequest request) {
        String mobile = request.getParameter("mobile");
        if (!StringUtils.hasText(mobile)) {
            // "" | null
            // TODO 手机号不能为空
            throw new RuntimeException();
        } else {
            // "something"
            return NopGrant.builder()
                    .providerUserId(mobile)
                    .secret(request.getParameter("password"))
                    .build();
        }
    }
}
