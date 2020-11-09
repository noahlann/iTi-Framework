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

package org.lan.iti.common.security.endpoint.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 身份相关的常量
 *
 * @author NorthLan
 * @date 2020-11-09
 * @url https://noahlan.com
 */
public interface PassportConstants {
    // 前缀
    String PREFIX_PASSPORT_SERVICE = "passport_";

    // 参数
    String REDIRECT_URI = "redirect_uri";
    String GRANT_TYPE = "grant_type";
    String CLIENT_ID = "client_id";
    String CLIENT_SECRET = "client_secret";

    // keys
    String ACCESS_TOKEN = "access_token";
    String EXPIRES_IN = "expires_in";
    String REFRESH_TOKEN = "refresh_token";
    String LICENSE = "license";
    String ACTIVE = "active";

    // 前端返回键列表
    List<String> RETURN_KEYS = Arrays.asList(
            ACCESS_TOKEN,
            EXPIRES_IN,
            LICENSE,
            ACTIVE);

    // 缓存相关 keys
    String KEY_CACHE_PREFIX = "passport:";
    String KEY_ACCESS_TO_REFRESH = "access_to_refresh:";
}
