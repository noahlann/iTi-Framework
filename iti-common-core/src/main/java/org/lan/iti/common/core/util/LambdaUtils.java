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
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

/**
 * Java 8 lambda 工具类
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@UtilityClass
public class LambdaUtils {

    /**
     * 获取Field名称
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldName(IFunction<T, R> getter) {
        return getter.getImplFieldName();
    }

    /**
     * 获取Field名称
     * <p>
     * 转下划线形式
     * </p>
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldNameCamel(IFunction<T, R> getter) {
        return StrUtil.toUnderlineCase(getFieldName(getter));
    }
}
