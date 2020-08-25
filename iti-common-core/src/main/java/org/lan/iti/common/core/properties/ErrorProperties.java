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

package org.lan.iti.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 错误码配置类
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = ErrorProperties.PREFIX)
public class ErrorProperties {
    public static final String PREFIX = "iti.error";

    /**
     * 是否开启全局统一异常码规范
     */
    private boolean enabled = false;

    /**
     * 固定标识
     */
    private Integer mark = 0;

    /**
     * 参数错误简化模式
     */
    private boolean argumentsImplicitMode = true;
}
