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

package org.lan.iti.cloud.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = ApiResultWrapperProperties.PREFIX)
public class ApiResultWrapperProperties {
    public static final String PREFIX = "iti.web.response.wrapper";

    /**
     * 是否启用wrapper增强
     */
    public boolean enabled = true;

    /**
     * 排除的包列表
     */
    public List<String> excludePackages = new ArrayList<>();

    /**
     * 排除的类列表
     */
    public List<String> excludeClasses = new ArrayList<>();
}
