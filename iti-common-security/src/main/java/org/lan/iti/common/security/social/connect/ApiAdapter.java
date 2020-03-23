/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.social.connect;

import org.lan.iti.common.security.social.exception.ApiException;

/**
 * Api适配器,作为 {@link Connection} 与 API 的桥接
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public interface ApiAdapter<A> {
    /**
     * Implements {@link Connection#test()} for connections to the given API.
     * @param api the API binding
     * @return true if the API is functional, false if not
     */
    boolean test(A api);

    /**
     * Sets values for {@link ConnectionKey#getProviderUserId()}, {@link Connection#getDisplayName()},
     * {@link Connection#getProfileUrl()}, and {@link Connection#getImageUrl()} for connections to the given API.
     * @param api the API binding
     * @param values the connection values to set
     * @throws ApiException if there is a problem fetching connection information from the provider.
     */
    void setConnectionValues(A api, ConnectionValues values);

    // TODO fetchUserProfile

    /**
     * Implements {@link Connection#updateStatus(String)} for connections to the given API.
     * If the provider does not have a status concept calling this method should have no effect.
     * @param api the API binding
     * @param message the status message
     * @throws ApiException if there is a problem updating the user's status on the provider.
     */
    void updateStatus(A api, String message);
}
