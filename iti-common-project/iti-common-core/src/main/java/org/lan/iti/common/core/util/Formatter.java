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

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 字符串格式化工具
 * <p>
 * 类似slf4j，使用{}作为占位符
 * </p>
 *
 * @author NorthLan
 * @date 2020-03-16
 * @url https://noahlan.com
 */
@Slf4j
public class Formatter {
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    /**
     * 格式化字符串
     *
     * @param pattern 匹配串
     * @param args    参数组
     */
    public static String format(final String pattern, final Object... args) {
        return arrayFormat(pattern, args);
    }

    /**
     * 格式化字符串
     *
     * @param pattern 匹配串
     * @param args    数据
     * @return 已格式化字符串
     */
    public static String arrayFormat(final String pattern, final Object[] args) {
        if (args == null) {
            return pattern;
        }
        int i = 0;
        int j;
        StringBuilder sb = new StringBuilder(pattern.length() + 50);
        int L;
        for (L = 0; L < args.length; ++L) {
            j = pattern.indexOf(DELIM_STR, i);
            if (j == -1) {
                if (i == 0) {
                    return pattern;
                } else {
                    sb.append(pattern, i, pattern.length());
                    return sb.toString();
                }
            } else {
                if (isEscapedDelimeter(pattern, j)) {
                    if (!isDoubleEscaped(pattern, j)) {
                        L--;
                        sb.append(pattern, i, j - 1);
                        sb.append(DELIM_START);
                        i = j + 1;
                    } else {
                        sb.append(pattern, i, j - 1);
                        deeplyAppendParameter(sb, args[L], new HashMap<>());
                        i = j + 2;
                    }
                } else {
                    sb.append(pattern, i, j);
                    deeplyAppendParameter(sb, args[L], new HashMap<>());
                    i = j + 2;
                }
            }
        }
        // 将最后一组{}后的数据添加
        sb.append(pattern, i, pattern.length());
        return sb.toString();
    }

    /**
     * 是否转义
     *
     * @param messagePattern      匹配串
     * @param delimeterStartIndex 检测起点下标
     */
    private static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == ESCAPE_CHAR;
    }

    /**
     * 是否双转义
     *
     * @param messagePattern      匹配串
     * @param delimeterStartIndex 检测起点下标
     */
    private static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
    }

    /**
     * 向串中添加参数
     *
     * @param sb      构建器
     * @param o       参数
     * @param seenMap 暂存Object的map
     */
    private static void deeplyAppendParameter(StringBuilder sb, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            sb.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(sb, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                booleanArrayAppend(sb, (boolean[]) o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(sb, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(sb, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(sb, (short[]) o);
            } else if (o instanceof int[]) {
                intArrayAppend(sb, (int[]) o);
            } else if (o instanceof long[]) {
                longArrayAppend(sb, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(sb, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(sb, (double[]) o);
            } else {
                objectArrayAppend(sb, (Object[]) o, seenMap);
            }
        }
    }

    private static void safeObjectAppend(StringBuilder sb, Object o) {
        try {
            String oAsString = o.toString();
            sb.append(oAsString);
        } catch (Throwable t) {
            log.warn("Formatter: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
            sb.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuilder sb, Object[] a, Map<Object[], Object> seenMap) {
        sb.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sb, a[i], seenMap);
                if (i != len - 1)
                    sb.append(", ");
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            sb.append("...");
        }
        sb.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sb, boolean[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void byteArrayAppend(StringBuilder sb, byte[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void charArrayAppend(StringBuilder sb, char[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void shortArrayAppend(StringBuilder sb, short[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void intArrayAppend(StringBuilder sb, int[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void longArrayAppend(StringBuilder sb, long[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void floatArrayAppend(StringBuilder sb, float[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sb, double[] a) {
        sb.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sb.append(a[i]);
            if (i != len - 1)
                sb.append(", ");
        }
        sb.append(']');
    }
}
