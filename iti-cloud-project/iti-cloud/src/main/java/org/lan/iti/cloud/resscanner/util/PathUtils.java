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

package org.lan.iti.cloud.resscanner.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.resscanner.model.PatternInfo;
import org.lan.iti.cloud.resscanner.model.ResourceDefinition;

import java.util.Comparator;

/**
 * 路径工具
 *
 * @author NorthLan
 * @date 2020-05-19
 * @url https://noahlan.com
 */
@UtilityClass
public class PathUtils {

    public Comparator<ResourceDefinition> getPatternComparator(String path) {
        return new AntPatternComparator(path);
    }

    private static class AntPatternComparator implements Comparator<ResourceDefinition> {
        private final String path;

        public AntPatternComparator(String path) {
            this.path = path;
        }

        @Override
        public int compare(ResourceDefinition o1, ResourceDefinition o2) {
            if (StrUtil.isBlank(o1.getUrl()) || StrUtil.isBlank(o2.getUrl())) {
                return 0;
            }
            PatternInfo info1 = new PatternInfo(o1.getUrl());
            PatternInfo info2 = new PatternInfo(o2.getUrl());

            if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
                return 0;
            } else if (info1.isLeastSpecific()) {
                return 1;
            } else if (info2.isLeastSpecific()) {
                return -1;
            }

            boolean pattern1EqualsPath = o1.getUrl().equals(this.path);
            boolean pattern2EqualsPath = o1.getUrl().equals(this.path);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            } else if (pattern1EqualsPath) {
                return -1;
            } else if (pattern2EqualsPath) {
                return 1;
            }

            if (info1.isPrefixPattern() && info2.isPrefixPattern()) {
                return info2.getLength() - info1.getLength();
            } else if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0) {
                return 1;
            } else if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
                return -1;
            }

            if (info1.getTotalCount() != info2.getTotalCount()) {
                return info1.getTotalCount() - info2.getTotalCount();
            }

            if (info1.getLength() != info2.getLength()) {
                return info2.getLength() - info1.getLength();
            }

            if (info1.getSingleWildcards() < info2.getSingleWildcards()) {
                return -1;
            } else if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
                return 1;
            }

            if (info1.getUriVars() < info2.getUriVars()) {
                return -1;
            } else if (info2.getUriVars() < info1.getUriVars()) {
                return 1;
            }

            return 0;
        }
    }
}
