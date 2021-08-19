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

package org.lan.iti.iha.security.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * IHA cache
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface Cache {

    /**
     * set cache
     *
     * @param key   Cache Key
     * @param value Cache Value after serialization
     */
    void put(String key, Object value);

    /**
     * Set the cache and specify the expiration time of the cache
     *
     * @param key      Cache key
     * @param value    Cache value after serialization
     * @param expire   The expiration time of the cache
     * @param timeUnit time unit
     */
    void put(String key, Object value, long expire, TimeUnit timeUnit);

    /**
     * Get an item from the cache, non-transactional
     *
     * @param key Cache key
     * @return Cache value
     */
    Object get(String key);

    /**
     * 判断key是否存在
     *
     * @param key cache key
     * @return true if key exists
     */
    boolean containsKey(String key);

    /**
     * return all keys
     *
     * @return all keys
     */
    Collection<String> keys();

    /**
     * remove items from the cache
     *
     * @param keys cache keys
     */
    void evict(String... keys);

    /**
     * clear cache
     */
    void clear();
}
