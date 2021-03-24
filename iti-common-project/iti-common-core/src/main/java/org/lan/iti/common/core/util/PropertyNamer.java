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

import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.exception.CheckedException;

import java.util.Locale;

/**
 * 属性命名工具类
 *
 * @author NorthLan
 * @date 2020-04-24
 * @url https://noahlan.com
 */
@UtilityClass
public class PropertyNamer {

    public String methodToProperty(String name, boolean failFast) {
        if (name == null) {
            return null;
        }
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else if (failFast) {
            throw new CheckedException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    public boolean isProperty(String name) {
        return isGetter(name) || isSetter(name);
    }

    public boolean isGetter(String name) {
        return (name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2);
    }

    public boolean isSetter(String name) {
        return name.startsWith("set") && name.length() > 3;
    }
}
