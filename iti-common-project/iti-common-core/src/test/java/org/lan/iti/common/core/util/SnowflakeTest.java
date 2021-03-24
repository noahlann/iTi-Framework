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

package org.lan.iti.common.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
public class SnowflakeTest {

    @Test
    public void snowFlakeTest() {
        Snowflake snowflake = new Snowflake(0, 0, true);
        List<Long> list = new CopyOnWriteArrayList<>();

        ExecutorService executor = new ThreadPoolExecutor(100, 1000, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());

        int totalNumberOfTasks = 100000;
        CountDownLatch latch = new CountDownLatch(totalNumberOfTasks);
        for (int i = 0; i < totalNumberOfTasks; ++i) {
            executor.execute(() -> {
                list.add(snowflake.nextId());
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Long> repeat = list.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println(repeat);
        System.out.println(list.size());
    }
}
