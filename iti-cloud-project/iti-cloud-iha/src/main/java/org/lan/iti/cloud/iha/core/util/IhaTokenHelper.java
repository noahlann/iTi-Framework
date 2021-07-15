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

package org.lan.iti.cloud.iha.core.util;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.kisso.security.token.SSOToken;
import org.lan.iti.cloud.iha.core.IhaConstants;
import org.lan.iti.cloud.iha.core.context.IhaAuthentication;
import org.lan.iti.cloud.iha.sso.IhaSsoUtil;

import java.util.Map;

/**
 * Iha user token helper, responsible for processing the token after the user logs in successfully
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class IhaTokenHelper {


    public static void saveUserToken(String userId, String token) {
        IhaAuthentication.getContext().getCache().set(IhaConstants.USER_TOKEN_KEY.concat(userId), token);
    }

    public static String getUserToken(String userId) {
        return (String) IhaAuthentication.getContext().getCache().get(IhaConstants.USER_TOKEN_KEY.concat(userId));
    }

    public static void removeUserToken(String userId) {
        IhaAuthentication.getContext().getCache().removeKey(IhaConstants.USER_TOKEN_KEY.concat(userId));
    }

    public static Map<String, Object> checkToken(String token) {
        SSOToken ssoToken = IhaSsoUtil.parse(token);
        if (ObjectUtil.isNull(ssoToken)) {
            return null;
        }
        String cacheKey = IhaConstants.USER_TOKEN_KEY.concat(ssoToken.getId());
        if (!IhaAuthentication.getContext().getCache().containsKey(cacheKey)) {
            return null;
        }
        return ssoToken.getClaims();
    }
}
