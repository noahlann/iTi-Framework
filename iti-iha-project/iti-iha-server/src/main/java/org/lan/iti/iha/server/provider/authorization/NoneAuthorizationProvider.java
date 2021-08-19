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

package org.lan.iti.iha.server.provider.authorization;

import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.security.IhaServerRequestParam;

import java.util.Map;

/**
 * When the value of {@code response_type} is {@code none}, the {@code code},{@code id_token} and {@code token} is not returned from the authorization endpoint,
 * if there is a state, it is returned as it is.
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#none">None Response Type</a>
 */
public class NoneAuthorizationProvider extends AbstractAuthorizationProvider {
    @Override
    public boolean matches(String params) {
        return ResponseType.NONE.getType().equalsIgnoreCase(params);
    }

    @Override
    protected void process(Map<String, Object> result, IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        // do nothing...
    }
}
