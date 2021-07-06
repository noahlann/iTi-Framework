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

import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaUserRepository {

    /**
     * Get user info by userid.
     *
     * @param userId userId of the business system
     * @return IhaUser
     */
    default IhaUser getById(String userId) {
        return null;
    }

    /**
     * Get user info by username.
     * <p>
     * It is suitable for the {@code jap-simple} module
     *
     * @param username username of the business system
     * @return IhaUser
     */
    default IhaUser getByName(String username) {
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
     * It is suitable for the {@code jap-social} module
     *
     * @param platform social platform
     * @param uid      social user id
     * @return IhaUser
     */
    default IhaUser getByPlatformAndUid(String platform, String uid) {
        return null;
    }

    /**
     * Save the social login user information to the database and return IhaUser
     * <p>
     * It is suitable for the {@code jap-social} module
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
     * It is suitable for the {@code jap-oauth2} module
     *
     * @param platform  oauth2 platform name
     * @param userInfo  The basic user information returned by the OAuth platform
     * @param tokenInfo The token information returned by the OAuth platform, developers can store tokens
     *                  , type {@code com.fujieid.jap.oauth2.helper.AccessToken}
     * @return When saving successfully, return {@code IhaUser}, otherwise return {@code null}
     */
    default IhaUser createAndGetOauth2User(String platform, Map<String, Object> userInfo, Object tokenInfo) {
        return null;
    }
}
