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

package org.lan.iti.cloud.resscanner.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.core.util.StringUtil;

/**
 * 编码工具类
 *
 * @author NorthLan
 * @date 2020-04-21
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class CodeUtils {
    private static final String DEFAULT_CONTROLLER_SUFFIX = "Controller";

    /**
     * 获取控制器短名称
     * <p>xxxController的xxx</p>
     *
     * @param clazz 控制器类
     * @return 短名称
     */
    public String getCtrShortName(Class<?> clazz, String ctrSuffix) {
        if (StrUtil.isBlank(ctrSuffix)) {
            ctrSuffix = DEFAULT_CONTROLLER_SUFFIX;
        }
        String simpleName = clazz.getSimpleName();
        int ctrIdx = simpleName.indexOf(ctrSuffix);
        if (ctrIdx == -1) {
            // 控制器名称不规范
            log.warn("控制器: {} 名称不规范, 应以[{}]结尾", simpleName, ctrSuffix);
            return simpleName;
        }
        return simpleName.substring(0, ctrIdx);
    }

    /**
     * 获取资源编码
     *
     * @param delimiter       分隔符
     * @param applicationName 服务名
     * @param moduleCode      模块代码
     * @param code            编码
     */
    public String getResourceCode(String delimiter, String applicationName, String moduleCode, String code) {
        if (StrUtil.isBlank(delimiter)) {
            throw new IllegalArgumentException("资源分隔符为空,请检查参数");
        }
        if (StrUtil.isBlank(applicationName) || StrUtil.isBlank(moduleCode)) {
            throw new IllegalArgumentException(Formatter.format("获取资源标识失败 服务编码:{} 模块编码:{}", applicationName, moduleCode));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(applicationName)
                .append(delimiter)
                .append(StrUtil.lowerFirst(moduleCode));
        if (StrUtil.isNotBlank(code)) {
            sb.append(delimiter).append(code);
        }
        return sb.toString();
    }

    /**
     * url转换为通配符
     * <p>
     * /test/ -> /test/**
     * /test -> /test/**"
     * /test/* -> /test/**" 且给予警告提示
     * /test/** -> /test/**
     * test/ -> /test/**
     * test -> /test/**
     * <p>
     * /test/* /xx -> /test/* /xx/**
     * <p>
     * /test/xx* -> null 不允许此类url存在
     * /test/*** -> null 不允许
     * </p>
     *
     * @param url 未支持通配形式的url
     * @return 通配 **
     */
    public String convertCtrWildcardUrl(String url) {
        if (StrUtil.isBlank(url)) {
            throw new IllegalArgumentException(Formatter.format("URL [{}] 错误,无法转换为通配符形式", url));
        }
        StringBuilder sb = new StringBuilder();
        int lastIdx = url.lastIndexOf('/');
        int firstIdx = url.indexOf('/');
        if (lastIdx == -1) {
            return sb.append('/').append(url).append('/').append("**").toString();
        } else if (lastIdx == 0) {
            return sb.append(url).append('/').append("**").toString();
        } else {
            if (firstIdx != 0) {
                sb.append('/');
            }
            // idx -> last
            sb.append(url);

            String lastUrl = url.substring(lastIdx + 1);
            int lastLength = lastUrl.length();
            if (lastLength == 0) {
                return sb.append("**").toString();
            }
            int repeatCount = StringUtil.repeatCount(lastUrl, '*');
            if (lastLength == repeatCount) {
                if (repeatCount > 2) {
                    // warning
                    log.warn("URL [{}] 两位以上通配,不允许此类型", url);
                    throw new IllegalArgumentException(Formatter.format("URL [{}] 两位以上通配,不允许此类型", url));
                }
                if (repeatCount == 1) {
                    log.warn("URL [{}] 通配类型需注意, 转换为 [{}*] 存储", url, url);
                    return sb.append('*').toString();
                } else {
                    return sb.toString();
                }
            }
            // 不等
            if (repeatCount > 0) {
                throw new IllegalArgumentException(Formatter.format("URL [{}] 通配混杂,不允许此类型", url));
            }
            return sb.append('/').append("**").toString();
        }
    }
}
