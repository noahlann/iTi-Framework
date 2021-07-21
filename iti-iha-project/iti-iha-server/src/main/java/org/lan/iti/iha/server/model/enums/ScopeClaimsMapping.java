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

package org.lan.iti.iha.server.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The relationship table between scope and user attributes. For a specific scope, only part of the user attributes can be obtained
 * <p>
 * This scope value requests access to the End-User's default profile Claims,
 * </p>
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#ScopeClaims" target="_blank">5.4.  Requesting Claims using Scope Values</a>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ScopeClaimsMapping {
    /**
     * which are: name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, gender, birthdate, zoneinfo, locale, and updated_at.
     */
    PROFILE(Arrays.asList("name", "family_name", "given_name", "middle_name", "nickname", "preferred_username", "profile", "picture", "website", "gender", "birthdate", "zoneinfo", "locale", "and updated_at")),

    /**
     * This scope value requests access to the email and email_verified Claims.
     */
    EMAIL(Arrays.asList("email", "email_verified")),

    /**
     * This scope value requests access to the phone_number and phone_number_verified Claims.
     */
    PHONE(Arrays.asList("phone_number", "phone_number_verified")),

    /**
     * This scope value requests access to the address Claim.
     */
    ADDRESS(Collections.singletonList("address")),

    /**
     * This scope value requests access to the roles Claim.
     */
    ROLES(Collections.singletonList("roles")),
    ;
    private final List<String> claims;
}
