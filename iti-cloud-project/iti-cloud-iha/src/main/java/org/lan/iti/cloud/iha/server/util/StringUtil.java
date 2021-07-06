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

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class StringUtil extends StrUtil {
    /**
     * Convert string to list
     *
     * @param text       The string to be converted
     * @param splitRegex Regular expression to split string
     * @return List of strings (de-duplicated)
     */
    public static Set<String> convertStrToList(String text, String splitRegex) {
        Set<String> result = new TreeSet<>();
        if (text != null && text.trim().length() > 0) {
            String[] tokens = text.split(splitRegex);
            result.addAll(Arrays.asList(tokens));
        }
        return result;
    }

    /**
     * Convert string to list
     *
     * @param text The string to be converted
     * @return List of strings (de-duplicated)
     */
    public static Set<String> convertStrToList(String text) {
        return convertStrToList(text, "[\\s+]");
    }

    /**
     * If the {@code str} does not end in {@code suffix}, then {@code suffix} is appended after {@code str};
     * If {@code suffix} is already included, return directly to {@code str}
     *
     * @param str    str
     * @param suffix Content to be added
     * @return String
     */
    public static String appendIfNotEndWith(String str, String suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str;
        }
        return str.endsWith(suffix) ? str : str + suffix;
    }

    /**
     * string to map, str format is{@code xxx=xxx&xxx=xxx}
     *
     * @param input The string to be converted
     * @return map
     */
    public static Map<String, String> parseStringToMap(String input) {
        Map<String, String> res = null;
        if (input.contains("&")) {
            String[] fields = input.split("&");
            res = new HashMap<>((int) (fields.length / 0.75 + 1));
            for (String field : fields) {
                if (field.contains("=")) {
                    String[] keyValue = field.split("=");
                    res.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null);
                }
            }
        } else if (input.contains("=")) {
            String[] keyValue = input.split("=");
            res = new HashMap<>((int) (keyValue.length / 0.75 + 1));
            res.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null);
        } else {
            res = new HashMap<>(0);
        }
        return res;
    }

    /**
     * Map to string, the format of the converted string is {@code xxx=xxx&xxx=xxx}
     *
     * @param params Map to be converted
     * @param encode Whether to encode the value of the map
     * @return String
     */
    public static String parseMapToString(Map<String, String> params, boolean encode) {
        if (null == params || params.isEmpty()) {
            return "";
        }
        List<String> paramList = new ArrayList<>();
        params.forEach((k, v) -> {
            if (null == v) {
                paramList.add(k + "=");
            } else {
                paramList.add(k + "=" + (encode ? URLUtil.encode(v) : v));
            }
        });
        return String.join("&", paramList);
    }
}
