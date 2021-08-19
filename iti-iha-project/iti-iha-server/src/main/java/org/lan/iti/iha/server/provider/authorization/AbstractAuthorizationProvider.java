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

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.security.IhaServerRequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础抽象类，包装 通用state注入
 *
 * @author NorthLan
 * @date 2021/8/17
 * @url https://blog.noahlan.com
 */
public abstract class AbstractAuthorizationProvider implements AuthorizationProvider {

    @Override
    public Map<String, Object> process(IhaServerRequestParam param,
                                       String responseType,
                                       ClientDetails clientDetails,
                                       UserDetails userDetails,
                                       String issuer) {
        Map<String, Object> result = new HashMap<>(8);
        /*
        REQUIRED if the "state" parameter was present in the client
         authorization request.  The exact value received from the
         client.
         */
        if (StringUtil.isNotEmpty(param.getState())) {
            result.put(OAuth2ParameterNames.STATE, param.getState());
        }
        this.process(result, param, responseType, clientDetails, userDetails, issuer);
        return result;
    }

    protected abstract void process(Map<String, Object> result,
                                    IhaServerRequestParam param,
                                    String responseType,
                                    ClientDetails clientDetails,
                                    UserDetails userDetails,
                                    String issuer);
}
