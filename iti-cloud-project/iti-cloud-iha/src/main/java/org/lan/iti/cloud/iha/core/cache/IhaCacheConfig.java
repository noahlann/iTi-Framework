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

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class IhaCacheConfig {
    /**
     * The cache expiration time is 1 day by default
     */
    public static long timeout = TimeUnit.DAYS.toMillis(7);

    /**
     * Turn on the timed task of clearing the local memory cache.
     * After it is turned on, the {@link IhaLocalCache#pruneCache()} method will be called to automatically clear the cache.
     * If you customize the implemented jap cache interface, you can ignore this config.
     */
    public static boolean schedulePrune = true;
}
