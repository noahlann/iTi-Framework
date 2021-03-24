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

package org.lan.iti.cloud.ddd.runtime;

import java.util.List;
import java.util.function.Predicate;

/**
 * 常用的扩展点执行的归约器实现.
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
public abstract class AbstractReducer<R> implements IReducer<R> {

    /**
     * 执行第一个匹配的扩展点.
     *
     * @param predicate 断言，判断是否找到了第一个匹配的扩展点
     * @param <R>       扩展点方法的返回值类型
     * @return 扩展点返回值
     */
    public static <R> AbstractReducer<R> firstOf(final Predicate<R> predicate) {
        return new AbstractReducer<R>() {
            @Override
            public R reduce(List<R> accumulatedResults) {
                R result = null;
                if (accumulatedResults != null && !accumulatedResults.isEmpty()) {
                    // 此时accumulatedResults只会有一个元素
                    result = accumulatedResults.get(0);
                }
                return result;
            }

            @Override
            public boolean shouldStop(List<R> accumulatedResults) {
                boolean result = false;
                if (accumulatedResults != null && !accumulatedResults.isEmpty()) {
                    result = predicate.test(accumulatedResults.get(accumulatedResults.size() - 1));
                }
                return result;
            }
        };
    }

    /**
     * 执行所有的扩展点，并获取期望的结果.
     *
     * @param predicate expected result predicate. if null, always return null
     * @param <R>       扩展点方法的返回值类型
     * @return the value that satisfies predicate. if none satisfied, returns null
     */
    public static <R> AbstractReducer<R> all(Predicate<R> predicate) {
        return new AbstractReducer<R>() {
            @Override
            public R reduce(List<R> accumulatedResults) {
                if (predicate == null) {
                    return null;
                }

                for (R r : accumulatedResults) {
                    if (predicate.test(r)) {
                        return r;
                    }
                }

                return null;
            }

            @Override
            public boolean shouldStop(List<R> accumulatedResults) {
                return false;
            }
        };
    }
}
