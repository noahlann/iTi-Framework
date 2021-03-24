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

package org.lan.iti.cloud.http;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.core.support.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Enum转换器
 * <p>作用于IEnum接口</p>
 * <p>RequestBody不使用此转换器</p>
 * <p>
 * <p>需使用@JsonCreater</p>
 *
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
public class StringEnumConverterFactory implements ConverterFactory<String, IEnum<?>> {

    @NonNull
    @Override
    public <T extends IEnum<?>> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnum<>(targetType);
    }


    private static class StringToEnum<T extends IEnum<?>> implements Converter<String, T> {

        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        @Nullable
        public T convert(@NonNull String source) {
            for (T enumConstant : enumType.getEnumConstants()) {
                if (StrUtil.equals(source, String.valueOf(enumConstant.getCode()))) {
                    return enumConstant;
                }
            }
            return null;
        }
    }
}
