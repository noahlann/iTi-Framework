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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 配置工具类
 *
 * @author NorthLan
 * @date 2020-04-22
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class PropertiesUtils {

    private Environment environment;

    /**
     * 获取配置
     *
     * @param key 键
     * @return 获取到的原始String配置
     */
    public String get(String key) {
        if (environment == null) {
            ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
            if (applicationContext == null) {
                log.warn("无法获取上下文Environment");
                return null;
            }
            environment = applicationContext.getEnvironment();
        }
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("Key不能为null或空白字符串");
        }
        return environment.getProperty(key);
    }

    /**
     * 获取配置
     *
     * @param key        键
     * @param defaultStr 默认值
     * @return 获取到的原始String配置
     */
    public String getOrDefault(String key, String defaultStr) {
        String value = get(key);
        if (StrUtil.isEmpty(value)) {
            return defaultStr;
        }
        return value;
    }

    /**
     * 获取Int类型的配置参数
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("ConstantConditions")
    public Integer getInteger(String key) {
        String value = get(key);
        if (StrUtil.isNotEmpty(value)) {
            return Integer.parseInt(value);
        }
        return null;
    }
}
