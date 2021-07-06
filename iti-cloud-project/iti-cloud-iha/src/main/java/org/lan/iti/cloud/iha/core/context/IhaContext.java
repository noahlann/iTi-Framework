/*
 * Copyright (c) 2020-2040, 北京符节科技有限公司 (support@fujieid.com & https://www.fujieid.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lan.iti.cloud.iha.core.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.config.IhaConfig;
import org.lan.iti.cloud.iha.core.store.IhaUserStore;

/**
 * The context of jap.
 * <p>
 * Persist jap user store, jap cache and jap config in memory to facilitate the management of jap user data.
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0.0
 * @since 1.0.0
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
