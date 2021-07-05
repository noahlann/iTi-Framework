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

package org.lan.iti.cloud.iha.core.cache;

import java.io.Serializable;

/**
 * IHA cache
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaCache {

    /**
     * set cache
     *
     * @param key   Cache Key
     * @param value Cache Value after serialization
     */
    void set(String key, Serializable value);

    /**
     * Set the cache and specify the expiration time of the cache
     *
     * @param key     Cache key
     * @param value   Cache value after serialization
     * @param timeout The expiration time of the cache, in milliseconds
     */
    void set(String key, Serializable value, long timeout);

    /**
     * Get cache value
     *
     * @param key Cache key
     * @return Cache value
     */
    Serializable get(String key);

    /**
     * Determine whether a key exists in the cache
     *
     * @param key Cache key
     * @return boolean
     */
    boolean containsKey(String key);

    /**
     * Delete the key from the cache
     *
     * @param key Cache key
     */
    void removeKey(String key);
}
