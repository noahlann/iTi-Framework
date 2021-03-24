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

package org.lan.iti.common.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <p>
 * 第一位为未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * <p>
 * 并且可以通过生成的id反推出生成时间,datacenterId和workerId
 * <p>
 * 参考：http://www.cnblogs.com/relucent/p/4955340.html
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Slf4j
public class Snowflake implements Serializable {
    private static final long serialVersionUID = -1L;

    private long twepoch;
    private final long workerIdBits = 5L;
    private final long dataCenterIdBits = 5L;

    // 最大支持机器节点数0~31，一共32个
    @SuppressWarnings("FieldCanBeLocal")
    private final long maxWorkerId = ~(-1L << workerIdBits);
    // 最大支持数据中心节点数0~31，一共32个
    @SuppressWarnings("FieldCanBeLocal")
    private final long maxDataCenterId = ~(-1L << dataCenterIdBits);
    // 序列号12位
    private final long sequenceBits = 12L;
    // 机器节点左移12位
    private final long workerIdShift = sequenceBits;
    // 数据中心节点左移17位
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒数左移22位
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    @SuppressWarnings("FieldCanBeLocal")
    private final long sequenceMask = ~(-1L << sequenceBits);// 4095

    private final long workerId;
    private final long dataCenterId;
    private final boolean useSystemClock;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public Snowflake() {
        this.dataCenterId = getDefaultDataCenterId(maxDataCenterId);
        this.workerId = getDefaultWorkerId(dataCenterId, maxWorkerId);
        this.useSystemClock = false;
        init(null);
    }

    /**
     * 构造
     *
     * @param workerId     终端ID
     * @param dataCenterId 数据中心ID
     */
    public Snowflake(long workerId, long dataCenterId) {
        this(workerId, dataCenterId, false);
    }

    /**
     * 构造
     *
     * @param workerId         终端ID
     * @param dataCenterId     数据中心ID
     * @param isUseSystemClock 是否使用{@link SystemClock} 获取当前时间戳
     */
    public Snowflake(long workerId, long dataCenterId, boolean isUseSystemClock) {
        this(null, workerId, dataCenterId, isUseSystemClock);
    }

    /**
     * @param epochDate        初始化时间起点（null表示默认起始日期）,后期修改会导致id重复,如果要修改连workerId dataCenterId，慎用
     * @param workerId         工作机器节点id
     * @param dataCenterId     数据中心id
     * @param isUseSystemClock 是否使用{@link SystemClock} 获取当前时间戳
     */
    public Snowflake(Date epochDate, long workerId, long dataCenterId, boolean isUseSystemClock) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.useSystemClock = isUseSystemClock;
        init(epochDate);
    }

    private void init(Date epochDate) {
        if (null != epochDate) {
            this.twepoch = epochDate.getTime();
        } else {
            // Thu, 04 Nov 2010 01:42:54 GMT
            this.twepoch = 1288834974657L;
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(Formatter.format("workerId 不能大于 {} 或小于 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(Formatter.format("dataCenterId 不能大于 {} 或小于 0", maxDataCenterId));
        }
    }

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public long getWorkerId(long id) {
        return id >> workerIdShift & ~(-1L << workerIdBits);
    }

    /**
     * 根据Snowflake的ID，获取数据中心id
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDataCenterId(long id) {
        return id >> dataCenterIdShift & ~(-1L << dataCenterIdBits);
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public long getGenerateDateTime(long id) {
        return (id >> timestampLeftShift & ~(-1L << 41L)) + twepoch;
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    public synchronized long nextId() throws IllegalStateException, RuntimeException {
        long timestamp = genTime();

        // 如果服务器时间有问题(时钟后退) 报错。
        // 闰秒问题
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = genTime();
                    if (timestamp < lastTimestamp) {
                        throw new IllegalStateException(Formatter.format("系统时钟被回退了，请确认服务器时间设定. {}ms 内无法再次生成id", lastTimestamp - timestamp));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalStateException(Formatter.format("系统时钟被回退了，请确认服务器时间设定. {}ms 内无法再次生成id", lastTimestamp - timestamp));
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((timestamp - twepoch) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 下一个ID string形态
     */
    public synchronized String nextIdStr() throws IllegalStateException {
        return String.valueOf(nextId());
    }

    // region internal helpers

    /**
     * 获取默认workerId
     */
    private static long getDefaultWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StrUtil.isNotBlank(name)) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split(StringPool.AT)[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取默认dataCenterId
     */
    private long getDefaultDataCenterId(long maxDataCenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDataCenterId + 1);
                }
            }
        } catch (Exception e) {
            log.warn(" getDefaultDataCenterId: " + e.getMessage());
        }
        return id;
    }

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private long genTime() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }
    // endregion
}
