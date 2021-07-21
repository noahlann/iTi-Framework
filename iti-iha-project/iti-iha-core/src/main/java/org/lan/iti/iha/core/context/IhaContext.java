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
package org.lan.iti.iha.core.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lan.iti.iha.core.cache.IhaCache;
import org.lan.iti.iha.core.config.IhaConfig;
import org.lan.iti.iha.core.store.IhaUserStore;

/**
 * The context of iha.
 * <p>
 * Persist iha user store, iha cache and iha config in memory to facilitate the management of iha user data.
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@AllArgsConstructor
public class IhaContext {
    /**
     * user store
     */
    private IhaUserStore userStore;

    /**
     * cache
     */
    private IhaCache cache;

    /**
     * configuration.
     */
    private IhaConfig config;
}
