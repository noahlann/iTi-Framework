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

package org.lan.iti.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * minio 配置信息
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = "iti.minio")
public class MinioProperties {
    /**
     * minio 服务地址 http://ip:port
     */
    private String url;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 端点配置
     */
    private Endpoint endpoint = new Endpoint();

    /**
     * 默认桶 名称列表
     * <p>
     * 不存在指定名称桶时，服务启动时会自动创建桶
     * </p>
     */
    private List<String> bucketNames = new ArrayList<>();

    @Data
    static class Endpoint {
        /**
         * 是否开启端点
         */
        private boolean enabled = false;

        /**
         * 端点name
         * <p>
         * 作用于 @RequestMapping
         * 将会自动拼接为 @RequestMapping("name/minio")
         * </p>
         */
        private String name;
    }
}
