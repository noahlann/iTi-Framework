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

package org.lan.iti.iha.sso;

import com.baomidou.kisso.security.token.SSOToken;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class IhaSsoUtil {

    public SSOToken createSsoToken(String userId, String username, HttpServletRequest request) {
        return SSOToken.create()
                .setId(userId)
                .setIssuer(username)
                .setIp(request)
                .setUserAgent(request)
                .setTime(System.currentTimeMillis());
    }

    public String createToken(String userId, String username, HttpServletRequest request) {
        return createSsoToken(userId, username, request).getToken();
    }

    public SSOToken parse(String token) {
        return SSOToken.parser(token);
    }
}
