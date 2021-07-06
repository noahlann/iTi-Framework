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

package org.lan.iti.cloud.iha.server.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {
    /**
     * Convert timestamp to LocalDateTime
     *
     * @param second     Long type timestamp, in seconds
     * @param zoneOffset Time zone, default is {@code +8}
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime ofEpochSecond(Long second, ZoneOffset zoneOffset) {
        if (zoneOffset == null) {
            return LocalDateTime.ofEpochSecond(second, 0, ZoneOffset.ofHours(8));
        } else {
            return LocalDateTime.ofEpochSecond(second, 0, zoneOffset);
        }
    }

    /**
     * Get the current Date
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nowDate() {
        return LocalDateTime.now();
    }
}
