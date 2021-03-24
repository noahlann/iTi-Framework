/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.cloud.scanner.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.lan.iti.common.core.constants.PrefixConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 资源扫描器配置类
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(PrefixConstants.SCANNER_PREFIX)
public class ScannerProperties {
    /**
     * 被扫描的服务名，例：通用权限管理服务
     */
    private String serviceName;

    /**
     * 资源代码 连接符
     */
    private String delimiter = "$";

    /**
     * 控制器后缀
     */
    private String ctrSuffix = "Controller";

    /**
     * 资源报告
     */
    private Report report = new Report();

    @Data
    @NoArgsConstructor
    public static class Report {
        /**
         * 是否启用资源报告服务
         */
        private boolean enabled = false;
    }
}
