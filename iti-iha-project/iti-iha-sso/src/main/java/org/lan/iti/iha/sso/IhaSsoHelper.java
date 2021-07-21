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

import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.security.token.SSOToken;
import lombok.experimental.UtilityClass;
import org.lan.iti.iha.sso.config.IhaSsoConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class IhaSsoHelper {
    public String login(String userId,
                        String username,
                        IhaSsoConfig ihaSsoConfig,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        ihaSsoConfig = ihaSsoConfig == null ? new IhaSsoConfig() : ihaSsoConfig;
        resetKissoConfig(ihaSsoConfig);

        SSOToken ssoToken = IhaSsoUtil.createSsoToken(userId, username, request);
        SSOHelper.setCookie(request, response, ssoToken, true);
        return ssoToken.getToken();
    }

    public void initKissoConfig(IhaSsoConfig ihaSsoConfig) {
        resetKissoConfig(ihaSsoConfig);
    }

    private void resetKissoConfig(IhaSsoConfig ihaSsoConfig) {
        // reset kisso
        SSOConfig config = SSOHelper.getSsoConfig();
        config.setCookieDomain(ihaSsoConfig.getCookieDomain())
                .setCookieName(ihaSsoConfig.getCookieName())
                .setParamReturnUrl(ihaSsoConfig.getParamReturnUrl())
                .setCookieMaxAge(ihaSsoConfig.getCookieMaxAge());
        // SSOHelper.setSsoConfig(config);
    }

    /**
     * Check the login status to determine whether the current user exists in the cookie
     *
     * @param request current HTTP request
     * @return The ID of the current login user
     */
    public static String checkLogin(HttpServletRequest request) {
        SSOToken ssoToken = SSOHelper.getSSOToken(request);
        return null == ssoToken ? null : ssoToken.getId();
    }

    /**
     * Log out and clear cookie content
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        SSOHelper.clearLogin(request, response);
    }
}
