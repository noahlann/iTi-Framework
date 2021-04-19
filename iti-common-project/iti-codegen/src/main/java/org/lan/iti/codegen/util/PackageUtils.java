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

package org.lan.iti.codegen.util;

import lombok.experimental.UtilityClass;

/**
 * 包名工具
 *
 * @author NorthLan
 * @date 2021-04-06
 * @url https://noahlan.com
 */
@UtilityClass
public class PackageUtils {

    /**
     * 获取父包名
     * <p>1. 参数为空字符串或null，返回空字符串</p>
     * <p>2. 找不到包分隔符情况下，返回原始字符串</p>
     *
     * @param pkgName 原始包名
     * @return 父包名
     */
    public String parent(String pkgName) {
        if (pkgName == null || pkgName.length() <= 0) {
            return "";
        }
        pkgName = pkgName.replaceAll("/", ".");
        int lastIndex = pkgName.lastIndexOf('.');
        if (lastIndex == -1) {
            return pkgName;
        }
        return pkgName.substring(0, pkgName.lastIndexOf('.'));
    }

    /**
     * 获取子包
     * <p>
     * <ul>
     *     <li>1. {@param pkgName}为null且{@param childPkgName}为null，返回空字符串</li>
     *     <li>2. {@param pkgName}为null且{@param childPkgName}不为null，返回{@param childPkgName}</li>
     *     <li>3. {@param childPkgName}为null或为空字符串，返回{@param pkgName}</li>
     *     <li>4. {@param pkgName}为空字符串，返回{@param childPkgName}，否则返回pkgName.childPkgName</li>
     * </ul>
     *
     * @param pkgName    包
     * @param childPkgName 子包名
     * @return 子包全限定名
     */
    public String child(String pkgName, String childPkgName) {
        if (pkgName == null) {
            return childPkgName == null ? "" : childPkgName;
        }
        if (childPkgName == null || childPkgName.length() <= 0) {
            return pkgName;
        }
        return pkgName.length() <= 0 ? childPkgName : pkgName + "." + childPkgName;
    }
}
