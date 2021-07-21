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

package org.lan.iti.iha.server.util;

import lombok.experimental.UtilityClass;
import org.lan.iti.iha.core.util.RequestUtil;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.config.IhaServerConfig;
import org.lan.iti.iha.server.exception.IhaServerException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class EndpointUtil {

    public static String getIssuer(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        if (config.isEnableDynamicIssuer() && null == request) {
            throw new IhaServerException("The second-level domain name verification has been enabled, the HTTP request cannot be empty");
        }
        return config.isEnableDynamicIssuer() ? RequestUtil.getFullDomainName(request) : config.getIssuer();
    }


    public static String getLoginUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getLoginUrl();
    }

    public static String getErrorUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getErrorUrl();
    }

    public static String getAuthorizeUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getAuthorizeUrl();
    }

    public static String getAuthorizeAutoApproveUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getAuthorizeAutoApproveUrl();
    }

    public static String getTokenUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getTokenUrl();
    }

    public static String getUserinfoUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getUserinfoUrl();
    }

    public static String getRegistrationUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getRegistrationUrl();
    }

    public static String getEndSessionUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getEndSessionUrl();
    }

    public static String getCheckSessionUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getCheckSessionUrl();
    }

    public static String getLogoutRedirectUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getLogoutRedirectUrl();
    }

    public static String getJwksUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getJwksUrl();
    }

    public static String getDiscoveryUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        return getIssuer(request) + config.getDiscoveryUrl();
    }

    public static String getLoginPageUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        if (config.isExternalLoginPageUrl()) {
            return config.getLoginPageUrl();
        }
        return getIssuer(request) + config.getLoginPageUrl();
    }

    public static String getConfirmPageUrl(HttpServletRequest request) {
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        if (config.isExternalConfirmPageUrl()) {
            return config.getConfirmPageUrl();
        }
        return getIssuer(request) + config.getConfirmPageUrl();
    }
}
