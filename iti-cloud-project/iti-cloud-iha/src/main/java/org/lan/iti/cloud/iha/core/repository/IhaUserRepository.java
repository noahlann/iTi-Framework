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

package org.lan.iti.cloud.iha.core.repository;

import org.lan.iti.cloud.iha.core.IhaUser;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.core.model.AbstractCredentials;

import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaUserRepository {

    /**
     * Get user info by params
     *
     * @param credentials Credentials
     * @param config      AuthenticateConfig
     * @return IhaUser
     */
    default IhaUser getByCredentials(AbstractCredentials credentials, AuthenticateConfig config) {
        return null;
    }

    /**
     * Get user info by userId. (SSO)
     *
     * @param userId userId of the business system
     * @return IhaUser
     */
    default IhaUser getById(String userId) {
        return null;
    }

    /**
     * Verify that the password entered by the user matches
     * <p>
     * It is suitable for the {@code iha-simple} module
     *
     * @param password The password in the HTML-based login form
     * @param user     User information that is queried by the user name in the HTML form
     * @return {@code boolean} When true is returned, the password matches, otherwise the password is wrong
     */
    default boolean validPassword(String password, IhaUser user) {
        return false;
    }

    /**
     * Get user information in the current system by social platform and social user id
     * <p>
     * It is suitable for the {@code iha-social} module
     *
     * @param platform social platform，refer to {@code me.zhyd.oauth.config.AuthSource#getName()}
     * @param uid      social user id
     * @return IhaUser
     */
    default IhaUser getByPlatformAndUid(String platform, String uid) {
        return null;
    }

    /**
     * Save the social login user information to the database and return IhaUser
     * <p>
     * It is suitable for the {@code iha-social} module
     *
     * @param userInfo User information obtained through justauth third-party login, type {@code me.zhyd.oauth.model.AuthUser}
     * @return When saving successfully, return {@code IhaUser}, otherwise return {@code null}
     */
    default IhaUser createAndGetSocialUser(Object userInfo) {
        return null;
    }

    /**
     * Save the oauth login user information to the database and return IhaUser
     * <p>
     * It is suitable for the {@code iha-oauth2} module
     *
     * @param platform  oauth2 platform name
     * @param userInfo  The basic user information returned by the OAuth platform
     * @param tokenInfo The token information returned by the OAuth platform, developers can store tokens
     *                  , type {@code org.lan.iti.cloud.iha.oauth2.token.AccessToken}
     * @return When saving successfully, return {@code IhaUser}, otherwise return {@code null}
     */
    default IhaUser createAndGetOAuth2User(String platform, Map<String, Object> userInfo, Object tokenInfo) {
        return null;
    }
}
