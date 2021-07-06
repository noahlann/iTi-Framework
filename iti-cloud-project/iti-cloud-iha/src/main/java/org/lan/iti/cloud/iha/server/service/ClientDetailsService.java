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

package org.lan.iti.cloud.iha.server.service;

import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.ClientDetails;

import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public interface ClientDetailsService {
    /**
     * Query client information through client id
     *
     * @param clientId Client application id
     * @return ClientDetail
     */
    default ClientDetails getByClientId(String clientId) {
        throw new IhaServerException("Not implemented `IdsClientDetailService.getByClientId(String)`");
    }

    /**
     * Add client
     *
     * @param clientDetails Client application details
     * @return ClientDetail
     */
    default ClientDetails add(ClientDetails clientDetails) {
        throw new IhaServerException("Not implemented `IdsClientDetailService.add(ClientDetail)`");
    }

    /**
     * Modify the client
     *
     * @param clientDetails Client application details
     * @return ClientDetail
     */
    default ClientDetails update(ClientDetails clientDetails) {
        throw new IhaServerException("Not implemented `IdsClientDetailService.update(ClientDetail)`");
    }

    /**
     * Delete client by primary key
     *
     * @param id Primary key of the client application
     * @return boolean
     */
    default boolean removeById(String id) {
        throw new IhaServerException("Not implemented `IdsClientDetailService.removeById(String)`");
    }

    /**
     * Delete client by client id
     *
     * @param clientId Client application id
     * @return ClientDetail
     */
    default boolean removeByClientId(String clientId) {
        throw new IhaServerException("Not implemented `IdsClientDetailService.removeByClientId(String)`");
    }

    /**
     * Get all client detail
     *
     * @return List
     */
    default List<ClientDetails> getAllClientDetail() {
        throw new IhaServerException("Not implemented `IdsClientDetailService.getAllClientDetail()`");
    }
}
