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

package org.lan.iti.iha.server.config;

import lombok.Data;

/**
 * UrlProperties
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 */
@Data
public class UrlProperties {
    private String tokenUrl = "/oauth2/token";
    private String errorUrl = "/oauth2/error";
    private String authorizeUrl = "/oauth2/authorize";
    private String authorizeAutoApproveUrl = "/oauth2/authorize/auto";
    private String loginUrl = "/oauth2/login";
    private String userinfoUrl = "/oauth2/userinfo";
    private String registrationUrl = "/oauth2/registration";
    private String endSessionUrl = "/oauth2/logout";
    private String checkSessionUrl = "/oauth2/check_session";
    private String logoutRedirectUrl = "/";
    private String jwksUrl = "/.well-known/jwks.json";
    private String discoveryUrl = "/.well-known/openid-configuration";
    private String confirmUrl = "/oauth2/confirm";
}
