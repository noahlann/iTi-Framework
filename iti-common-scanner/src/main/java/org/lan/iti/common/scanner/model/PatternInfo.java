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

package org.lan.iti.common.scanner.model;

import org.springframework.lang.Nullable;

import java.util.regex.Pattern;

/**
 * Value class that holds information about the pattern, e.g. number of
 * occurrences of "*", "**", and "{" pattern elements.
 *
 * @author NorthLan
 * @date 2020-05-11
 * @url https://noahlan.com
 */
public class PatternInfo {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?}");

    @Nullable
    private final String pattern;

    private int uriVars;

    private int singleWildcards;

    private int doubleWildcards;

    private boolean catchAllPattern;

    private boolean prefixPattern;

    @Nullable
    private Integer length;

    public PatternInfo(@Nullable String pattern) {
        this.pattern = pattern;
        if (this.pattern != null) {
            initCounters();
            this.catchAllPattern = this.pattern.equals("/**");
            this.prefixPattern = !this.catchAllPattern && this.pattern.endsWith("/**");
        }
        if (this.uriVars == 0) {
            this.length = (this.pattern != null ? this.pattern.length() : 0);
        }
    }

    protected void initCounters() {
        int pos = 0;
        if (this.pattern != null) {
            while (pos < this.pattern.length()) {
                if (this.pattern.charAt(pos) == '{') {
                    this.uriVars++;
                    pos++;
                } else if (this.pattern.charAt(pos) == '*') {
                    if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
                        this.doubleWildcards++;
                        pos += 2;
                    } else if (pos > 0 && !this.pattern.substring(pos - 1).equals(".*")) {
                        this.singleWildcards++;
                        pos++;
                    } else {
                        pos++;
                    }
                } else {
                    pos++;
                }
            }
        }
    }

    public int getUriVars() {
        return this.uriVars;
    }

    public int getSingleWildcards() {
        return this.singleWildcards;
    }

    public int getDoubleWildcards() {
        return this.doubleWildcards;
    }

    public boolean isLeastSpecific() {
        return (this.pattern == null || this.catchAllPattern);
    }

    public boolean isPrefixPattern() {
        return this.prefixPattern;
    }

    public int getTotalCount() {
        return this.uriVars + this.singleWildcards + (2 * this.doubleWildcards);
    }

    /**
     * Returns the length of the given pattern, where template variables are considered to be 1 long.
     */
    public int getLength() {
        if (this.length == null) {
            this.length = (this.pattern != null ?
                    VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length() : 0);
        }
        return this.length;
    }
}