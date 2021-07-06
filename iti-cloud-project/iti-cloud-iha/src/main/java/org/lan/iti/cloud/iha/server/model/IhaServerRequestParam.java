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

package org.lan.iti.cloud.iha.server.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.lan.iti.cloud.iha.core.model.Mapped;
import org.lan.iti.cloud.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.cloud.iha.oauth2.pkce.PkceParams;
import org.lan.iti.cloud.iha.oidc.OidcParameterNames;
import org.lan.iti.cloud.iha.server.IhaServerConstants;

import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class IhaServerRequestParam extends Mapped {
    private static final long serialVersionUID = -2276207187946980706L;

    private String usernameField;
    private String passwordField;

    public IhaServerRequestParam(Map<String, ?> map) {
        super(map);
    }

    public void setScope(String scopes) {
        map(OAuth2ParameterNames.SCOPE, scopes);
    }

    public String getClientId() {
        return map(OAuth2ParameterNames.CLIENT_ID);
    }

    public String getClientSecret() {
        return map(OAuth2ParameterNames.CLIENT_SECRET);
    }

    public String getGrantType() {
        return map(OAuth2ParameterNames.GRANT_TYPE);
    }

    public String getCode() {
        return map(OAuth2ParameterNames.CODE);
    }

    public String getRedirectUri() {
        return map(OAuth2ParameterNames.REDIRECT_URI);
    }

    public String getScope() {
        return map(OAuth2ParameterNames.SCOPE);
    }

    public String getState() {
        return map(OAuth2ParameterNames.STATE);
    }

    public String getAccessToken() {
        return map(OAuth2ParameterNames.ACCESS_TOKEN);
    }

    public String getRefreshToken() {
        return map(OAuth2ParameterNames.REFRESH_TOKEN);
    }

    public String getResponseType() {
        return map(OAuth2ParameterNames.RESPONSE_TYPE);
    }

    public String getUid() {
        return map(IhaServerConstants.UID);
    }

    public String getAutoApprove() {
        return map(IhaServerConstants.AUTOAPPROVE);
    }

    public String getUsername() {
        return map(StrUtil.isEmpty(usernameField) ? OAuth2ParameterNames.USERNAME : usernameField);
    }

    public String getPassword() {
        return map(StrUtil.isEmpty(passwordField) ? OAuth2ParameterNames.PASSWORD : passwordField);
    }

    public String getCodeVerifier() {
        return map(PkceParams.CODE_VERIFIER);
    }

    public String getCodeChallengeMethod() {
        return map(PkceParams.CODE_CHALLENGE_METHOD);
    }

    public String getCodeChallenge() {
        return map(PkceParams.CODE_CHALLENGE);
    }


    /*  The following are the parameters supported by the oidc protocol, referenced from: https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest     */

    /**
     * optional, The nonce parameter value needs to include per-session state and be unguessable to attackers
     */
    public String getNonce() {
        return map(OidcParameterNames.NONCE);
    }

    /**
     * Optional. The newly defined parameter of oidc (oauth 2.0 form post response mode) is used to specify how the authorization endpoint returns data.
     */
    public String getResponseMode() {
        return map(OidcParameterNames.RESPONSE_MODE);
    }

    /**
     * OPTIONAL. ASCII string value that specifies how the Authorization Server displays the authentication and consent user interface pages to the End-User. The defined values are:
     * <p>
     * <strong>page</strong> - The Authorization Server SHOULD display the authentication and consent UI consistent with a full User Agent page view.
     * If the display parameter is not specified, this is the default display mode.
     * <p>
     * <strong>popup</strong> - The Authorization Server SHOULD display the authentication and consent UI consistent with a popup User Agent window.
     * The popup User Agent window should be of an appropriate size for a login-focused dialog and should not obscure the entire window that it is popping up over.
     * <p>
     * <strong>touch</strong> - The Authorization Server SHOULD display the authentication and consent UI consistent with a device that leverages a touch interface.
     * <p>
     * <strong>wap</strong> - The Authorization Server SHOULD display the authentication and consent UI consistent with a "feature phone" type display.
     */
    public String getDisplay() {
        return map(OidcParameterNames.DISPLAY);
    }

    /**
     * OPTIONAL. Space delimited, case sensitive list of ASCII string values that specifies whether the Authorization Server prompts the End-User for reauthentication and consent. The defined values are:
     * <p>
     * <strong>none</strong> - The Authorization Server MUST NOT display any authentication or consent user interface pages.
     * An error is returned if an End-User is not already authenticated or the Client does not have pre-configured
     * consent for the requested Claims or does not fulfill other conditions for processing the request.
     * The error code will typically be login_required, interaction_required, or another code defined in Section 3.1.2.6.
     * This can be used as a method to check for existing authentication and/or consent.
     * <p>
     * <strong>login</strong> - The Authorization Server SHOULD prompt the End-User for reauthentication.
     * If it cannot reauthenticate the End-User, it MUST return an error, typically login_required.
     * <p>
     * <strong>consent</strong> - The Authorization Server SHOULD prompt the End-User for consent before returning information to the Client.
     * If it cannot obtain consent, it MUST return an error, typically consent_required.
     * <p>
     * <strong>select_account</strong> - The Authorization Server SHOULD prompt the End-User to select a user account.
     * This enables an End-User who has multiple accounts at the Authorization Server to select amongst
     * the multiple accounts that they might have current sessions for.
     * If it cannot obtain an account selection choice made by the End-User,
     * it MUST return an error, typically account_selection_required.
     */
    public String getPrompt() {
        return map(OidcParameterNames.PROMPT);
    }

    /**
     * Optional. Represents the valid time of the eu authentication information,
     * corresponding to the claim of auth time in the id token. For example,
     * if the setting is 20 minutes, if the time is exceeded, you need to guide eu to re-authenticate.
     */
    public String getAuthTime() {
        return map(OidcParameterNames.AUTH_TIME);
    }

    /**
     * Optional. For the previously issued id token, if the id token is verified and valid, it needs to return a normal response;
     * if there is an error, it returns a corresponding error prompt.
     */
    public String getIdTokenHint() {
        return map(OidcParameterNames.ID_TOKEN_HINT);
    }

    /**
     * Optional. Requested Authentication Context Class Reference values.
     * Space-separated string that specifies the acr values that the Authorization Server is being requested to use for processing this Authentication Request,
     * with the values appearing in order of preference
     */
    public String getAcr() {
        return map(OidcParameterNames.ACR);
    }

    public boolean isEnablePkce() {
        return !StrUtil.isEmpty(this.getCodeVerifier());
    }
}
