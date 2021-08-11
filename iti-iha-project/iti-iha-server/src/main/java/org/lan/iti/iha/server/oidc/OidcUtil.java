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

package org.lan.iti.iha.server.oidc;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.ReservedClaimNames;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.pkce.CodeChallengeMethod;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.model.OidcDiscovery;
import org.lan.iti.iha.oauth2.enums.ClientAuthenticationMethod;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.model.enums.TokenAlgorithms;
import org.lan.iti.iha.server.provider.ScopeProvider;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class OidcUtil {
    public static OidcDiscovery getOidcDiscovery(HttpServletRequest request) {
        String issuer = EndpointUtil.getIssuer(request);
        val builder = OidcDiscovery.builder();

        builder
                .issuer(issuer)
                .authorization_endpoint(EndpointUtil.getAuthorizeUrl(request))
                .token_endpoint(EndpointUtil.getTokenUrl(request))
                .userinfo_endpoint(EndpointUtil.getUserinfoUrl(request))
                .registration_endpoint(EndpointUtil.getRegistrationUrl(request))
                .end_session_endpoint(EndpointUtil.getEndSessionUrl(request))
                .check_session_iframe(EndpointUtil.getCheckSessionUrl(request))
                .jwks_uri(EndpointUtil.getJwksUrl(request))
                .grant_types_supported(GrantType.grantTypes())
                .response_modes_supported(Arrays.asList("fragment", "query"))
                .response_types_supported(ResponseType.responseTypes())
                .scopes_supported(ScopeProvider.getScopeCodes())
                .token_endpoint_auth_methods_supported(ClientAuthenticationMethod.getAllMethods())
                .request_object_signing_alg_values_supported(Arrays.asList(
                        TokenAlgorithms.NONE.getAlg(),
                        TokenAlgorithms.RS256.getAlg(),
                        TokenAlgorithms.ES256.getAlg()
                ))
                .userinfo_signing_alg_values_supported(Arrays.asList(
                        TokenAlgorithms.RS256.getAlg(),
                        TokenAlgorithms.ES256.getAlg()
                ))
                .id_token_signing_alg_values_supported(Arrays.asList(
                        TokenAlgorithms.RS256.getAlg(),
                        TokenAlgorithms.ES256.getAlg()
                ))
                .request_parameter_supported(true)
                .request_uri_parameter_supported(true)
                .require_request_uri_registration(false)
                .claims_parameter_supported(true)
                .subject_types_supported(Collections.singletonList("public"))
                .claims_supported(Arrays.asList(
                        ReservedClaimNames.ISSUER,
                        ReservedClaimNames.SUBJECT,
                        ReservedClaimNames.AUDIENCE,
                        ReservedClaimNames.EXPIRATION_TIME,
                        ReservedClaimNames.ISSUED_AT,
                        OidcParameterNames.NONCE,
                        OidcParameterNames.AUTH_TIME,
                        OAuth2ParameterNames.USERNAME
                ))
                .code_challenge_methods_supported(CodeChallengeMethod.getAllMethods());
        return builder.build();
    }

    public static String getJwksPublicKey(String identity) {
        String jwksJson = IhaServer.getContext().getIdentityService().getJwksJson(identity);
        JsonWebKeySet jsonWebKeySet = JwtUtil.IhaVerificationKeyResolver.createJsonWebKeySet(jwksJson);
        return jsonWebKeySet.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
    }
}
