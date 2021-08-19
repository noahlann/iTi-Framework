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
import org.lan.iti.iha.security.util.RequestUtil;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.config.IhaServerConfig;
import org.lan.iti.iha.server.config.UrlProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
            throw new SecurityException("The second-level domain name verification has been enabled, the HTTP request cannot be empty");
        }
        return config.isEnableDynamicIssuer() ?
                RequestUtil.getFullDomainName(request)
                        .concat(Optional.ofNullable(config.getContextPath()).orElse("")) :
                config.getIssuer();
    }


    public static String getLoginUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getLoginUrl();
    }

    public static String getErrorUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getErrorUrl();
    }

    public static String getAuthorizeUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getAuthorizeUrl();
    }

    public static String getAuthorizeAutoApproveUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getAuthorizeAutoApproveUrl();
    }

    public static String getTokenUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getTokenUrl();
    }

    public static String getUserinfoUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getUserinfoUrl();
    }

    public static String getRegistrationUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getRegistrationUrl();
    }

    public static String getEndSessionUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getEndSessionUrl();
    }

    public static String getCheckSessionUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getCheckSessionUrl();
    }

    public static String getLogoutRedirectUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getLogoutRedirectUrl();
    }

    public static String getJwksUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getJwksUrl();
    }

    public static String getDiscoveryUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        return getIssuer(request) + urlProperties.getDiscoveryUrl();
    }

    public static String getLoginPageUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        if (urlProperties.isExternalLoginPageUrl()) {
            return urlProperties.getLoginPageUrl();
        }
        return getIssuer(request) + urlProperties.getLoginPageUrl();
    }

    public static String getConfirmPageUrl(HttpServletRequest request) {
        UrlProperties urlProperties = IhaServer.getUrlProperties();
        if (urlProperties.isExternalConfirmPageUrl()) {
            return urlProperties.getConfirmPageUrl();
        }
        return getIssuer(request) + urlProperties.getConfirmPageUrl();
    }
}
