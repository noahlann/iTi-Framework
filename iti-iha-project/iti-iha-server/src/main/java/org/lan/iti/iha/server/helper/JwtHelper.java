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

package org.lan.iti.iha.server.helper;

import lombok.experimental.UtilityClass;
import org.jose4j.jwt.JwtClaims;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.OAuth2Constants;
import org.lan.iti.iha.security.jwt.JwtUtil;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.userdetails.UserDetailsHelper;
import org.lan.iti.iha.server.model.enums.ScopeClaimsMapping;

import java.util.List;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/8/13
 * @url https://blog.noahlan.com
 */
@UtilityClass
public class JwtHelper {

    public static String createJwtToken(String issuer,
                                        String clientId,
                                        UserDetails userDetails,
                                        String nonce,
                                        long expireIn) {
        return createJwtToken(issuer, clientId, userDetails, nonce, expireIn, null);
    }

    public static String createJwtToken(String issuer,
                                        String clientId,
                                        UserDetails userDetails,
                                        String nonce,
                                        long expireIn,
                                        String scope) {
        JwtClaims claims = JwtUtil.createClaims(issuer,
                clientId,
                userDetails == null ? clientId : userDetails.getId(),
                nonce,
                expireIn);

        setUserInfoClaim(userDetails, scope, claims);

        return JwtUtil.createJwtToken(claims, clientId);
    }

    public static void setUserInfoClaim(UserDetails userDetails, String scope, JwtClaims claims) {
        if (null != userDetails) {
            // If you include other claim reference: https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
            claims.setStringClaim("username", userDetails.getUsername());
            if (StringUtil.isNotEmpty(scope)) {
                // This scope value requests access to the End-User's default profile Claims,
                // which are: name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, gender, birthdate, zoneinfo, locale, and updated_at.
                List<String> scopes = StringUtil.split(scope, OAuth2Constants.SCOPE_SEPARATOR);
                Map<String, Object> userInfoMap = UserDetailsHelper.toMap(userDetails);
                if (scopes.contains("profile")) {
                    ScopeClaimsMapping scopeClaimsMapping = ScopeClaimsMapping.PROFILE;
                    List<String> claimList = scopeClaimsMapping.getClaims();
                    for (String claim : claimList) {
                        if (userInfoMap.containsKey(claim) && null != userInfoMap.get(claim)) {
                            claims.setClaim(claim, userInfoMap.get(claim));
                        }
                    }
                }
                // This scope value requests access to the email and email_verified Claims.
                if (scopes.contains("email")) {
                    ScopeClaimsMapping scopeClaimsMapping = ScopeClaimsMapping.EMAIL;
                    List<String> claimList = scopeClaimsMapping.getClaims();
                    for (String claim : claimList) {
                        if (userInfoMap.containsKey(claim) && null != userInfoMap.get(claim)) {
                            claims.setClaim(claim, userInfoMap.get(claim));
                        }
                    }
                }
                // This scope value requests access to the phone_number and phone_number_verified Claims.
                if (scopes.contains("phone")) {
                    ScopeClaimsMapping scopeClaimsMapping = ScopeClaimsMapping.PHONE;
                    List<String> claimList = scopeClaimsMapping.getClaims();
                    for (String claim : claimList) {
                        if (userInfoMap.containsKey(claim) && null != userInfoMap.get(claim)) {
                            claims.setClaim(claim, userInfoMap.get(claim));
                        }
                    }
                }
                // This scope value requests access to the address Claim.
                if (scopes.contains("address")) {
                    ScopeClaimsMapping scopeClaimsMapping = ScopeClaimsMapping.ADDRESS;
                    List<String> claimList = scopeClaimsMapping.getClaims();
                    for (String claim : claimList) {
                        if (userInfoMap.containsKey(claim) && null != userInfoMap.get(claim)) {
                            claims.setClaim(claim, userInfoMap.get(claim));
                        }
                    }
                }
                // This scope value requests access to the roles Claim.
                if (scopes.contains("roles")) {
                    ScopeClaimsMapping scopeClaimsMapping = ScopeClaimsMapping.ROLES;
                    List<String> claimList = scopeClaimsMapping.getClaims();
                    for (String claim : claimList) {
                        if (userInfoMap.containsKey(claim) && null != userInfoMap.get(claim)) {
                            claims.setClaim(claim, userInfoMap.get(claim));
                        }
                    }
                }
            }
        }
    }
}
