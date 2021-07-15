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
package org.lan.iti.cloud.iha.server.util;

import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.json.JsonUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwk.*;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.EcKeyUtil;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.lan.iti.cloud.iha.oidc.OidcParameterNames;
import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.config.IhaServerConfig;
import org.lan.iti.cloud.iha.server.config.JwtConfig;
import org.lan.iti.cloud.iha.server.exception.IhaTokenException;
import org.lan.iti.cloud.iha.server.exception.InvalidJwksException;
import org.lan.iti.cloud.iha.server.exception.InvalidTokenException;
import org.lan.iti.cloud.iha.server.model.UserDetails;
import org.lan.iti.cloud.iha.server.model.enums.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * simple JSON Web Key generator：https://mkjwk.org/?spm=a2c4g.11186623.2.33.4b2040ecxvsKD7
 * <p>
 * JWT Decoder: http://jwt.calebb.net/
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Slf4j
@UtilityClass
public class JwtUtil {

    /**
     * https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples
     *
     * @param clientId      Client Identifier
     * @param userDetails   User Profile
     * @param tokenExpireIn Id Token validity (seconds)
     * @param nonce         Random string
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return jwt token
     */
    public static String createJwtToken(String clientId, UserDetails userDetails, Long tokenExpireIn, String nonce, String issuer) {
        return createJwtToken(clientId, userDetails, tokenExpireIn, nonce, null, null, issuer);
    }

    /**
     * https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples
     *
     * @param clientId      Client Identifier
     * @param userDetails   User Profile
     * @param tokenExpireIn Id Token validity (seconds)
     * @param nonce         Random string
     * @param scopes        Scopes
     * @param responseType  Response Type
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return jwt token
     */
    public static String createJwtToken(String clientId, UserDetails userDetails, Long tokenExpireIn, String nonce, Set<String> scopes, String responseType, String issuer) {
        JwtClaims claims = new JwtClaims();

        // required
        // A unique identity of the person providing the authentication information. Usually an HTTPS URL (excl. queryString and Fragment)
        claims.setIssuer(issuer);
        // The LOGO of EU provided by ISS is unique within the scope of ISS. It is used by the RP to identify a unique user. The maximum length is 255 ASCII characters
        claims.setSubject(null == userDetails ? clientId : userDetails.getId());
        // Identify the audience for ID Token. OAuth2's client_ID must be included
        claims.setAudience(clientId);
        // Expiration time. ID Token beyond this time will become invalid and will no longer be authenticated
        claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis() + (tokenExpireIn * 1000)));
        // JWT build time
        claims.setIssuedAt(NumericDate.fromMilliseconds(System.currentTimeMillis()));

        // optional
        // The random string provided by the RP when it sends a request is used to mitigate replay attacks, and the ID Token can also be associated with the RP's own Session
        if (!StringUtil.isEmpty(nonce)) {
            claims.setStringClaim(OidcParameterNames.NONCE, nonce);
        }
        // Time of completion of EU certification. This Claim is required if the RP carries the max_AGE parameter when sending the AuthN request
//        claims.setClaim("auth_time", "auth_time");

        setUserInfoClaim(userDetails, scopes, responseType, claims);

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        JwtConfig jwtConfig = IhaServer.getContext().getIdentityService().getJwtConfig(clientId);
        if (null == jwtConfig) {
            throw new InvalidJwksException("Unable to create Jwt Token: jwt config cannot be empty.");
        }
        PublicJsonWebKey publicJsonWebKey = IdsVerificationKeyResolver.createPublicJsonWebKey(jwtConfig.getJwksKeyId(), jwtConfig.getJwksJson(), jwtConfig.getTokenSigningAlg());
        if (null == publicJsonWebKey) {
            throw new InvalidJwksException("Unable to create Jwt Token: Unable to create public json web key.");
        }
        // The JWT is signed using the private key
        jws.setKey(publicJsonWebKey.getPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(publicJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(jwtConfig.getTokenSigningAlg().getAlg());

        String idToken;

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        try {
            idToken = jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new IhaTokenException("Unable to create Jwt Token: " + e.getMessage());
        }

        return idToken;
    }

    private static void setUserInfoClaim(UserDetails userDetails, Set<String> scopes, String responseType, JwtClaims claims) {
        if (null != userDetails) {
            // If you include other claim reference: https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
            claims.setStringClaim("username", userDetails.getUsername());
            if (ObjectUtil.isNotNull(scopes) && ResponseType.ID_TOKEN.getType().equalsIgnoreCase(responseType)) {
                // This scope value requests access to the End-User's default profile Claims,
                // which are: name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, gender, birthdate, zoneinfo, locale, and updated_at.
                Map<String, Object> userInfoMap = JsonUtil.parseKv(JsonUtil.toJsonString(userDetails));
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

    public static Map<String, Object> parseJwtToken(String jwtToken) {
        JwtConfig jwtConfig = IhaServer.getContext().getIdentityService().getJwtConfig(null);
        if (null == jwtConfig) {
            throw new InvalidJwksException("Unable to parse Jwt Token: jwt config cannot be empty.");
        }

        PublicJsonWebKey publicJsonWebKey = IdsVerificationKeyResolver.createPublicJsonWebKey(jwtConfig.getJwksKeyId(), jwtConfig.getJwksJson(), jwtConfig.getTokenSigningAlg());
        if (null == publicJsonWebKey) {
            throw new InvalidJwksException("Unable to parse Jwt Token: Unable to create public json web key.");
        }
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                // whom the JWT needs to have been issued by
                // allow some leeway in validating time based claims to account for clock skew
                .setAllowedClockSkewInSeconds(30)
                // verify the signature with the public key
                .setVerificationKey(publicJsonWebKey.getPublicKey())
                // create the JwtConsumer instance
                .build();

        try {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
            return jwtClaims.getClaimsMap();

        } catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            log.error("Invalid Jwt Token : " + JsonUtil.toJsonString(e.getErrorDetails()), e);
            if (e.hasExpired()) {
                throw new InvalidTokenException(ErrorResponse.EXPIRED_TOKEN);
            }
            throw new InvalidTokenException(ErrorResponse.INVALID_TOKEN);
        }
    }

    public static Map<String, Object> validateJwtToken(String clientId, String userId, String jwtToken, String jwksUrl) {
        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        IhaServerConfig serverConfig = IhaServer.getIhaServerConfig();
        JwtConfig jwtConfig = IhaServer.getContext().getIdentityService().getJwtConfig(clientId);
        if (null == jwtConfig) {
            throw new InvalidJwksException("Unable to validate Jwt Token: jwt config cannot be empty.");
        }
        JwtConsumerBuilder jwtConsumerBuilder = new JwtConsumerBuilder();

        JwtVerificationType jwtVerificationType = jwtConfig.getJwtVerificationType();
        if (null != jwtVerificationType) {
            if (jwtVerificationType == JwtVerificationType.HTTPS_JWKS_ENDPOINT) {
                // The HttpsJwks retrieves and caches keys from a the given HTTPS JWKS endpoint.
                // Because it retains the JWKs after fetching them, it can and should be reused
                // to improve efficiency by reducing the number of outbound calls the the endpoint.
                VerificationKeyResolver verificationKeyResolver = new HttpsJwksVerificationKeyResolver(new HttpsJwks(jwksUrl));
                jwtConsumerBuilder.setVerificationKeyResolver(verificationKeyResolver);
            } else if (jwtVerificationType == JwtVerificationType.JWKS) {
                // There's also a key resolver that selects from among a given list of JWKs using the Key ID
                // and other factors provided in the header of the JWS/JWT.
                JsonWebKeySet jsonWebKeySet = IdsVerificationKeyResolver.createJsonWebKeySet(jwtConfig.getJwksJson());
                JwksVerificationKeyResolver jwksResolver = new JwksVerificationKeyResolver(jsonWebKeySet.getJsonWebKeys());
                jwtConsumerBuilder.setVerificationKeyResolver(jwksResolver);
            }
        }

        PublicJsonWebKey publicJsonWebKey = IdsVerificationKeyResolver.createPublicJsonWebKey(jwtConfig.getJwksKeyId(), jwtConfig.getJwksJson(), jwtConfig.getTokenSigningAlg());
        if (null == publicJsonWebKey) {
            throw new InvalidJwksException("Unable to verify Jwt Token: Unable to create public json web key.");
        }
        JwtConsumer jwtConsumer = jwtConsumerBuilder
                .setRequireIssuedAt()
                // the JWT must have an expiration time
                .setRequireExpirationTime()
                // the JWT must have a subject claim
                .setRequireSubject()
                // whom the JWT needs to have been issued by
                .setExpectedIssuer(serverConfig.getIssuer())
                .setExpectedSubject(StringUtil.isEmpty(userId) ? clientId : userId)
                // to whom the JWT is intended for
                .setExpectedAudience(clientId)
                // allow some leeway in validating time based claims to account for clock skew
                .setAllowedClockSkewInSeconds(30)
                // verify the signature with the public key
                .setVerificationKey(publicJsonWebKey.getPublicKey())
                // create the JwtConsumer instance
                .build();

        try {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
            return jwtClaims.getClaimsMap();

        } catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            log.error("Invalid Jwt Token! ", e);

            // Programmatic access to (some) specific reasons for JWT invalidity is also possible
            // should you want different error handling behavior for certain conditions.

            // Whether or not the JWT has expired being one common reason for invalidity
            if (e.hasExpired()) {
                try {
                    log.error("Jwt Token expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
                } catch (MalformedClaimException malformedClaimException) {
                    malformedClaimException.printStackTrace();
                }
                throw new InvalidTokenException(ErrorResponse.EXPIRED_TOKEN);
            }

            throw new InvalidTokenException(ErrorResponse.INVALID_TOKEN);
        }
    }

    public static class IdsVerificationKeyResolver {

        public static JsonWebKeySet createJsonWebKeySet(String jwksJson) {
            InvalidJwksException invalidJwksException = new InvalidJwksException(ErrorResponse.INVALID_JWKS);
            if (StringUtil.isEmpty(jwksJson)) {
                throw invalidJwksException;
            }

            JsonWebKeySet jsonWebKeySet;
            try {
                jsonWebKeySet = new JsonWebKeySet(jwksJson);
            } catch (JoseException e) {
                throw invalidJwksException;
            }
            return jsonWebKeySet;
        }

        public static PublicJsonWebKey createPublicJsonWebKey(String keyId, String jwksJson, TokenAlgorithms tokenAlgorithms) {
            tokenAlgorithms = null == tokenAlgorithms ? TokenAlgorithms.RS256 : tokenAlgorithms;
            JsonWebKeySet jsonWebKeySet = createJsonWebKeySet(jwksJson);

            switch (tokenAlgorithms.getKeyType()) {
                case RsaKeyUtil.RSA:
                    return (RsaJsonWebKey) jsonWebKeySet.findJsonWebKey(keyId, tokenAlgorithms.getKeyType(), Use.SIGNATURE, tokenAlgorithms.getAlg());
                case EcKeyUtil.EC:
                    return (EllipticCurveJsonWebKey) jsonWebKeySet.findJsonWebKey(keyId, tokenAlgorithms.getKeyType(), Use.SIGNATURE, tokenAlgorithms.getAlg());
                default:
                    return null;
            }
        }
    }
}
