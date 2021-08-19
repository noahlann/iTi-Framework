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

package org.lan.iti.iha.oauth2.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * AccessToken for client
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken implements Serializable {
    private static final long serialVersionUID = 5702257145706476772L;

    private String accessToken;
    private String refreshToken;
    private String idToken;

    private String tokenType;
    private Long expiresIn;
    private Long refreshTokenExpiresIn;
    private Long idTokenExpiresIn;
    private String scope;

    // ids
    private String uid; // 用户ID 框架自用
    private String userId; // 用户ID（twitter附带）
    private String openId;
    private String accessCode;
    private String unionId;


    /**
     * 小米附带属性
     */
    private String macAlgorithm;
    private String macKey;

    /**
     * 企业微信附带属性
     */
    private String code;

    /**
     * Twitter附带属性
     */
    private String oauthToken;
    private String oauthTokenSecret;
    private String screenName;
    private boolean oauthCallbackConfirmed;
}
