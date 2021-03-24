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

package org.lan.iti.cloud.loadbalancer.gray.support;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 灰度版本号传递工具 ,在非web 调用feign 传递之前手动setVersion
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@UtilityClass
public class NonWebVersionContextHolder {
    private final ThreadLocal<String> THREAD_LOCAL_VERSION = new TransmittableThreadLocal<>();

    /**
     * TTL 设置版本号
     *
     * @param version 版本号
     */
    public void setVersion(String version) {
        THREAD_LOCAL_VERSION.set(version);
    }

    /**
     * 获取TTL中的版本号
     *
     * @return 版本 || null
     */
    public String getVersion() {
        return THREAD_LOCAL_VERSION.get();
    }

    public void clear() {
        THREAD_LOCAL_VERSION.remove();
    }
}
