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

package org.lan.iti.iha.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationToken implements Serializable {
    private static final long serialVersionUID = 4062691954040845906L;

    private String accessToken;
    private String refreshToken;
    private String idToken;

    private String tokenType;

    @JsonProperty(OAuth2ParameterNames.EXPIRES_IN)
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn;
    private Long idTokenExpiresIn;

    @JsonIgnore
    private LocalDateTime accessTokenExpiration;

    @JsonIgnore
    private LocalDateTime refreshTokenExpiration;

    @JsonIgnore
    private LocalDateTime idTokenExpiration;

    private String scope;

    private String userId;
    private String openId;
    private String unionId;
    private String grantType;
    private String clientId;

}
