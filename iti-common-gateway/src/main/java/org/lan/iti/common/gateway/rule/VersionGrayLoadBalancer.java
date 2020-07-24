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

package org.lan.iti.common.gateway.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.Map;

/**
 * 基于客户端版本号灰度路由
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class VersionGrayLoadBalancer implements GrayLoadBalancer {
    private final DiscoveryClient discoveryClient;

    @Override
    public ServiceInstance choose(String serviceId, ServerHttpRequest request) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

        // 注册中心无相关实例,异常
        if (CollUtil.isEmpty(instances)) {
            log.warn("No instance available for {}", serviceId);
            throw new NotFoundException("No instance available for " + serviceId);
        }

        // 获取请求version，无则返回可用实例
        String reqVersion = request.getHeaders().getFirst(ITIConstants.VERSION);
        if (StrUtil.isBlank(reqVersion)) {
            return instances.get(RandomUtil.randomInt(instances.size()));
        }

        // 遍历可以实例元数据，若匹配则返回此实例
        for (ServiceInstance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            String targetVersion = MapUtil.getStr(metadata, ITIConstants.VERSION);
            if (reqVersion.equalsIgnoreCase(targetVersion)) {
                log.debug("gray request match success :{} {}", reqVersion, instance);
                return instance;
            }
        }
        return instances.get(RandomUtil.randomInt(instances.size()));
    }
}
