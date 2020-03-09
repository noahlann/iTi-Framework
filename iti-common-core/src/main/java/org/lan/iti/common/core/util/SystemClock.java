/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.util;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 *
 * <p>System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过，有人说是100倍左右）</p>
 * <p>System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道</p>
 * <p>后台定时更新时钟，JVM退出时，线程自动回收</p>
 * <p>10亿：43410,206,210.72815533980582%</p>
 * <p>1亿：4699,29,162.0344827586207%</p>
 * <p>1000万：480,12,40.0%</p>
 * <p>100万：50,10,5.0%</p>
 * <p>
 * see： http://git.oschina.net/yu120/sequence
 *
 * @author NorthLan
 * @date 2020-03-03
 * @url https://noahlan.com
 */
public class SystemClock {

    /**
     * 时钟更新间隔，单位毫秒
     */
    private final long period;
    /**
     * 现在时刻的毫秒数
     */
    private volatile long now;

    /**
     * 构造
     *
     * @param period 时钟更新间隔，单位毫秒
     */
    private SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        scheduleClockUpdating();
    }

    /**
     * 开启计时器线程
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now = System.currentTimeMillis(), period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @return 当前时间毫秒数
     */
    private long currentTimeMillis() {
        return now;
    }

    //------------------------------------------------------------------------ static

    /**
     * 单例持有器
     */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

    /**
     * 单例实例
     *
     * @return 单例实例
     */
    private static SystemClock instance() {
        return SystemClock.InstanceHolder.INSTANCE;
    }

    /**
     * @return 当前时间
     */
    public static long now() {
        return instance().currentTimeMillis();
    }

    /**
     * @return 当前时间字符串表现形式
     */
    public static String nowDate() {
        return new Timestamp(instance().currentTimeMillis()).toString();
    }
}