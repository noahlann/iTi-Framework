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

package org.lan.iti.iha.social.cache;

import me.zhyd.oauth.cache.AuthStateCache;
import org.lan.iti.iha.security.cache.Cache;
import org.lan.iti.iha.security.IhaSecurity;

import java.util.concurrent.TimeUnit;

/**
 * @author NorthLan
 * @date 2021/8/18
 * @url https://blog.noahlan.com
 */
public class SocialStateCache implements AuthStateCache {
    private final Cache cache;

    public SocialStateCache() {
        this(IhaSecurity.getContext().getCache());
    }

    public SocialStateCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void cache(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        cache.put(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(String key) {
        return String.valueOf(cache.get(key));
    }

    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
