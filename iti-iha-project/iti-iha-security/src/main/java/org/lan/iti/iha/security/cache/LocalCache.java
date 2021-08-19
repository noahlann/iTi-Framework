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

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 本地缓存
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class LocalCache implements Cache {
    private static final Map<String, CacheObj> LOCAL_CACHE = new ConcurrentHashMap<>();
    // 公平可重入读写锁
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock(true);
    private final Lock writeLock = cacheLock.writeLock();
    private final Lock readLock = cacheLock.readLock();

    public LocalCache() {
        if (IhaCacheConfig.schedulePrune) {
            this.schedulePrune(IhaCacheConfig.expire);
        }
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, IhaCacheConfig.expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(String key, Object value, long expire, TimeUnit timeUnit) {
        writeLock.lock();
        try {
            LOCAL_CACHE.put(key, new CacheObj(value, TimeUnit.MILLISECONDS.convert(expire, timeUnit)));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Object get(String key) {
        if (StrUtil.isEmpty(key)) {
            return null;
        }
        readLock.lock();
        try {
            CacheObj cacheObj = LOCAL_CACHE.get(key);
            if (null == cacheObj || cacheObj.isExpired()) {
                return null;
            }
            return cacheObj.getData();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        if (StrUtil.isEmpty(key)) {
            return false;
        }
        readLock.lock();
        try {
            CacheObj cacheObj = LOCAL_CACHE.get(key);
            return null != cacheObj && !cacheObj.isExpired();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<String> keys() {
        return LOCAL_CACHE.keySet();
    }

    @Override
    public void evict(String... keys) {
        for (String key : keys) {
            writeLock.lock();
            try {
                LOCAL_CACHE.remove(key);
            } finally {
                writeLock.unlock();
            }
        }

    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            LOCAL_CACHE.clear();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Start a scheduled task to clean up expired cache
     *
     * @param delay Interval duration, in milliseconds
     */
    public void schedulePrune(long delay) {
        AuthCacheScheduler.INSTANCE.schedule(this::pruneCache, delay);
    }

    /**
     * Clean up expired cache
     */
    public void pruneCache() {
        Iterator<CacheObj> values = LOCAL_CACHE.values().iterator();
        CacheObj cacheObj;
        while (values.hasNext()) {
            cacheObj = values.next();
            if (cacheObj.isExpired()) {
                values.remove();
            }
        }
    }

    /**
     * Cache scheduler
     */
    private enum AuthCacheScheduler {
        /**
         * AuthCacheScheduler
         */
        INSTANCE;

        private final AtomicInteger cacheTaskNumber = new AtomicInteger(1);
        private ScheduledExecutorService scheduler;

        AuthCacheScheduler() {
            create();
        }

        private void create() {
            this.shutdown();
            this.scheduler = new ScheduledThreadPoolExecutor(10, r -> new Thread(r, String.format("JustAuth-Task-%s", cacheTaskNumber.getAndIncrement())));
        }

        public void shutdown() {
            if (null != scheduler) {
                this.scheduler.shutdown();
            }
        }

        public void schedule(Runnable task, long delay) {
            this.scheduler.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Cache Object
     */
    private static class CacheObj implements Serializable {
        private static final long serialVersionUID = 7762617419326300303L;

        private final Object data;
        private final long expire;

        CacheObj(Object data, long expire) {
            this.data = data;
            // The actual expiration time is equal to the current time plus the validity period
            this.expire = System.currentTimeMillis() + expire;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > this.expire;
        }

        public Object getData() {
            return data;
        }

        public long getExpire() {
            return expire;
        }
    }
}
