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

package org.lan.iti.cloud.support;


import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名的线程工厂.
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_COUNT = new AtomicInteger(0);
    private final AtomicInteger threadCount = new AtomicInteger(1);

    private final ThreadGroup group;
    private final String namePrefix;
    private final boolean daemon;

    public NamedThreadFactory(String prefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + POOL_COUNT.getAndIncrement() + "-T-";
        this.daemon = daemon;
    }

    public static void setPoolCount(int count) {
        POOL_COUNT.set(count);
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        // {prefix}-{poolCount}-T-{threadCount}
        Thread t = new Thread(group, r, namePrefix + threadCount.getAndIncrement(), 0);
        t.setDaemon(daemon);
        return t;
    }
}