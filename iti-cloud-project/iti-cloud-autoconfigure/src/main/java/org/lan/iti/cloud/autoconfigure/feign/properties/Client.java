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

package org.lan.iti.cloud.autoconfigure.feign.properties;

import lombok.Data;

/**
 * 客户端配置
 * {@link org.springframework.cloud.openfeign.FeignClient}
 *
 * @author NorthLan
 * @date 2020-04-24
 * @url https://noahlan.com
 */
@Data
public class Client {
    private Class<?> clazz;
    private String name = "";
    private String contextId = "";
    private String qualifier = "";
    private String url = "";
    private boolean decode404 = false;
    private Class<?>[] configuration = new Class[0];
    private Class<?> fallback = void.class;
    private Class<?> fallbackFactory = void.class;
    private String path = "";
    private boolean primary = true;
}
