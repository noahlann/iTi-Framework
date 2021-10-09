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

package org.lan.iti.iha.oauth2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
public class AccessTokenHelperTest {
    private AccessToken accessToken;
    private Map<String, Object> accessTokenMap;
    private String accessTokenStr = "{\"access_token\":\"accessToken\",\"refresh_token\":\"refreshToken\",\"id_token\":\"idToken\",\"token_type\":\"tokenType\",\"expires_in\":12,\"refresh_token_expires_in\":12,\"id_token_expires_in\":12,\"scope\":\"openid\",\"uid\":\"123123\",\"user_id\":null,\"open_id\":null,\"access_code\":null,\"union_id\":null,\"mac_algorithm\":null,\"mac_key\":null,\"code\":null,\"oauth_token\":null,\"oauth_token_secret\":null,\"screen_name\":null,\"oauth_callback_confirmed\":false}";

    @BeforeEach
    public void setUp() {
        accessToken = AccessToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .idToken("idToken")
                .tokenType("tokenType")
                .expiresIn(12L)
                .refreshTokenExpiresIn(12L)
                .idTokenExpiresIn(12L)
                .scope("openid")
                .build();
        accessTokenMap = new HashMap<>();
        accessTokenMap.put("access_token", "accessToken");
        accessTokenMap.put("refresh_token", "refreshToken");
        accessTokenMap.put("id_token", "idToken");
        accessTokenMap.put("token_type", "tokenType");
        accessTokenMap.put("expires_in", 12L);
        accessTokenMap.put("refresh_token_expires_in", 12L);
        accessTokenMap.put("id_token_expires_in", 12L);
        accessTokenMap.put("scope", "openid");
        accessTokenMap.put("uid", "123123");
    }

    @Test
    public void test() {
        System.out.println(AccessTokenHelper.toMap(accessToken));
        System.out.println(AccessTokenHelper.toJsonString(accessToken));
        System.out.println(AccessTokenHelper.toAccessToken(accessTokenMap));
        System.out.println(AccessTokenHelper.toAccessToken(accessTokenStr));
    }
}
