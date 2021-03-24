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

package org.lan.iti.cloud.security.component;

import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 本地模式资源服务器的Token服务
 * <p>
 * 当前仅支持username一种形式
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@RequiredArgsConstructor
public class RedisTokenServices implements ResourceServerTokenServices {

    private final TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
        if (oAuth2Authentication == null) {
            return null;
        }

        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        if (!(oAuth2Authentication.getPrincipal() instanceof ITIUserDetails)) {
            return oAuth2Authentication;
        }

        // 根据 username 查询 spring cache 最新的值 并返回
        ITIUserDetails itiUser = (ITIUserDetails) oAuth2Authentication.getPrincipal();
        UserDetails userDetails = userDetailsService.loadUserByUsername(itiUser.getUsername());
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A",
                userDetails.getAuthorities());
        OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
