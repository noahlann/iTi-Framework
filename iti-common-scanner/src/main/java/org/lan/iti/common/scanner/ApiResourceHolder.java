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

package org.lan.iti.common.scanner;

import cn.hutool.core.util.StrUtil;
import lombok.val;
import org.lan.iti.common.scanner.exception.ScannerException;
import org.lan.iti.common.scanner.model.ResourceDefinition;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务资源保持器
 *
 * @author NorthLan
 * @date 2020-03-06
 * @url https://noahlan.com
 */
public final class ApiResourceHolder {
    /**
     * 以资源编码为标识的存放
     * code:resource
     */
    private static final Map<String, ResourceDefinition> RESOURCE_DEFINITION_MAP = new ConcurrentHashMap<>();

    /**
     * 以控制器编码,资源编码为标识的存放
     * controllerCode:[code:resource]
     */
    private static final Map<String, Map<String, ResourceDefinition>> RESOURCE_DEFINITION_MAP_CTR = new ConcurrentHashMap<>();

    /**
     * 以url为标识存放
     * url:resource
     */
    private static final Map<String, ResourceDefinition> RESOURCE_DEFINITION_MAP_URL = new ConcurrentHashMap<>();

    /**
     * 编码与中文名称的动态字典
     * controllerCode:controllerName
     */
    private static final Map<String, String> DICT_NAME_CODE = new ConcurrentHashMap<>();

    /**
     * 注册本服务资源
     *
     * @param resources 资源列表
     */
    public static void register(List<ResourceDefinition> resources) {
        resources.forEach(it -> {
            // TODO 检查每个资源情况？ code url name
            ResourceDefinition existsResource = getResource(it.getCode());
            if (existsResource != null) {
                // 扫描到重复资源(指代code重复)
                throw new ScannerException("服务中存在重复资源,请检查资源编码是否重复！提示：检查@ITIApi注解 已存在资源：{}, 新资源：{}",
                        existsResource, it);
            }
            // code-base
            RESOURCE_DEFINITION_MAP.put(it.getCode(), it);
            // url-base
            RESOURCE_DEFINITION_MAP_URL.put(it.getUrl(), it);
            // ctr-base
            String underLineCtrCode = StrUtil.toUnderlineCase(it.getCtrCode());
            Map<String, ResourceDefinition> ctrResources = RESOURCE_DEFINITION_MAP_CTR.get(underLineCtrCode);
            if (ctrResources == null) {
                ctrResources = new HashMap<>();
                ctrResources.put(it.getCode(), it);
                RESOURCE_DEFINITION_MAP_CTR.put(underLineCtrCode, ctrResources);
            } else {
                ctrResources.put(underLineCtrCode, it);
            }
            // dict
            bindDict(it.getCode(), it.getName());
        });
    }

    /**
     * 获取单个资源
     *
     * @param code 资源代码
     * @return 资源描述
     */
    @Nullable
    public static ResourceDefinition getResource(String code) {
        return RESOURCE_DEFINITION_MAP.get(code);
    }

    /**
     * 获取所有本服务资源
     */
    public static List<ResourceDefinition> getAllResources() {
        return new ArrayList<>(RESOURCE_DEFINITION_MAP.values());
    }

    /**
     * 获取资源
     *
     * @param code 控制器编码
     * @return 资源列表
     */
    public static List<ResourceDefinition> getResourcesByCtrCode(String code) {
        Map<String, ResourceDefinition> resourceMap = RESOURCE_DEFINITION_MAP_CTR.get(code);
        if (resourceMap == null) {
            return new ArrayList<>();
        }
        List<ResourceDefinition> result = new ArrayList<>();
        val iter = resourceMap.entrySet().iterator();
        while (iter.hasNext()) {
            val entry = iter.next();
            val value = entry.getValue();
            if (value == null) {
                iter.remove();
            } else {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * 获取资源名称
     *
     * @param code 资源编码
     * @return 资源名称
     */
    public static String getResourceName(String code) {
        return DICT_NAME_CODE.getOrDefault(code, "");
    }

    /**
     * 绑定字典
     *
     * @param code 资源编码
     * @param name 资源名称
     */
    public static void bindDict(String code, String name) {
        DICT_NAME_CODE.putIfAbsent(code, name);
    }

    /**
     * 获取模块化资源
     *
     * @return 模块化的资源，结构：[controllerCode - [apiCode-resource]]
     */
    public static Map<String, Map<String, ResourceDefinition>> getCtrResources() {
        return RESOURCE_DEFINITION_MAP_CTR;
    }

    /**
     * 获取资源的URL
     *
     * @param code 资源编码
     */
    @Nullable
    public static String getResourceUrl(String code) {
        ResourceDefinition resourceDefinition = getResource(code);
        if (resourceDefinition == null) {
            return null;
        }
        return resourceDefinition.getUrl();
    }

    /**
     * 通过url获取资源声明
     *
     * @param url url
     */
    @Nullable
    public static ResourceDefinition getResourceByUrl(String url) {
        return RESOURCE_DEFINITION_MAP_URL.get(url);
    }
}
