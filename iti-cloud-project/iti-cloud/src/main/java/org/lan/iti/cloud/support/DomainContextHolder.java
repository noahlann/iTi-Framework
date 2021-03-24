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

package org.lan.iti.cloud.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.lan.iti.common.core.constants.SecurityConstants;

/**
 * Domain持有器
 *
 * @author NorthLan
 * @date 2020-07-02
 * @url https://noahlan.com
 */
public final class DomainContextHolder {
    private static final TransmittableThreadLocal<String> THREAD_LOCAL_DOMAIN = new TransmittableThreadLocal<>();

    /**
     * 设置请求域
     *
     * @param domain 请求域
     */
    public static void setDomain(String domain) {
        THREAD_LOCAL_DOMAIN.set(domain);
    }

    /**
     * 获取请求域
     *
     * @return 当前请求持有的域
     */
    public static String getDomain() {
        String result = THREAD_LOCAL_DOMAIN.get();
        if (StrUtil.isBlank(result)) {
            setDomain(SecurityConstants.DEFAULT_DOMAIN);
        }
        return THREAD_LOCAL_DOMAIN.get();
    }

    /**
     * 清理
     */
    public static void clear() {
        THREAD_LOCAL_DOMAIN.remove();
    }

    /**
     * 当前请求链中是否存在domain信息
     *
     * @return 是否存在信息
     */
    public static boolean hasDomain() {
        return THREAD_LOCAL_DOMAIN.get() != null;
    }
}
