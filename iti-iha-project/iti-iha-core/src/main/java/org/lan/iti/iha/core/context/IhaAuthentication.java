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

package org.lan.iti.iha.core.context;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.json.util.Kv;
import lombok.Getter;
import lombok.Setter;
import org.lan.iti.iha.core.IhaUser;
import org.lan.iti.iha.core.cache.IhaCache;
import org.lan.iti.iha.core.config.IhaConfig;
import org.lan.iti.iha.core.result.IhaErrorCode;
import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.core.store.IhaUserStore;
import org.lan.iti.iha.core.util.IhaTokenHelper;
import org.lan.iti.iha.core.util.RequestUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class IhaAuthentication implements Serializable {
    private static final long serialVersionUID = 140344652845200177L;

    @Getter
    @Setter
    private static IhaContext context;

    /**
     * Get the currently logged in user
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return IhaUser
     */
    public static IhaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        if (context == null) {
            return null;
        }
        IhaUserStore userStore = context.getUserStore();
        if (userStore == null) {
            return null;
        }
        return userStore.get(request, response);
    }

    /**
     * Check whether the user is logged in. Reference method of use:
     * <p>
     * <code>
     * if(!IhaAuthentication.checkUser(request, response).isSuccess()) {
     * // Not logged in.
     * }
     * </code>
     * <p>
     * Is equivalent to the following code：
     *
     * <code>
     * IhaUser user = IhaAuthentication.getUser(request, response);
     * if (null == user) {
     * // Not logged in.
     * }
     * </code>
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return IhaResponse
     */
    public static IhaResponse checkUser(HttpServletRequest request, HttpServletResponse response) {
        IhaUser user = getUser(request, response);
        if (null == user) {
            return IhaResponse.error(IhaErrorCode.NOT_LOGGED_IN);
        }
        return IhaResponse.success(user);
    }

    /**
     * Verify the legitimacy of IHA Token
     *
     * @param token jwt token
     * @return Map
     */
    public static Map<String, Object> checkToken(String token) {
        if (null == context || ObjectUtil.isEmpty(token)) {
            return null;
        }
        IhaCache cache = context.getCache();
        if (null == cache) {
            return null;
        }
        Map<String, Object> tokenMap = IhaTokenHelper.checkToken(token);
        if (MapUtil.isNotEmpty(tokenMap)) {
            Kv kv = new Kv();
            kv.putAll(tokenMap);
            // Get the token creation time, multiplied by 1000 is the number of milliseconds
            long iat = kv.getLong("iat") * 1000;
            IhaConfig ihaConfig = context.getConfig();
            // Get token expiration time
            long tokenExpireTime = ihaConfig.getTokenExpireTime();
            // The token is available when the token creation time plus the token expiration time is later than the current time,
            // otherwise the token has expired
            if (new Date(iat + tokenExpireTime).after(new Date())) {
                return tokenMap;
            }
        }
        return null;
    }

    /**
     * logout
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return boolean
     */
    public static boolean logout(HttpServletRequest request, HttpServletResponse response) {
        IhaUserStore ihaUserStore = context.getUserStore();
        if (null == ihaUserStore) {
            return false;
        }
        ihaUserStore.remove(request, response);

        // Clear all cookie information
        Map<String, Cookie> cookieMap = RequestUtil.getCookieMap(request);
        if (MapUtil.isNotEmpty(cookieMap)) {
            cookieMap.forEach((key, cookie) -> {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            });
        }
        return true;
    }
}
