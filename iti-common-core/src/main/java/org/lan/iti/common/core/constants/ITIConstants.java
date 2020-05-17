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

package org.lan.iti.common.core.constants;

/**
 * 框架通用常量
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
public final class ITIConstants {
    /**
     * 请求号header标识
     */
    public static final String REQUEST_NO_HEADER_NAME = "Request-No";

    /**
     * header中的spanId，传递规则: request header中传递本服务的id
     */
    public static final String SPAN_ID_HEADER_NAME = "Span-Id";

    /**
     * header 中租户ID
     */
    public static final String TENANT_ID_HEADER_NAME = "TENANT_ID";

    /**
     * Feign 传递泛型的实际类名
     */
    public static final String FEIGN_GENERICS_HEADER_NAME = "ITI-FEIGN-GENERICS";

    /**
     * header 中版本信息
     */
    public static final String VERSION = "VERSION";

    /**
     * 默认租户ID = 0
     */
    public static final String DEFAULT_TENANT_ID = "0";

    /**
     * 编码
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 菜单树根节点ID
     */
    public static final String MENU_TREE_ROOT_ID = "-1";

    /**
     * 公共参数
     */
    public static final String PUBLIC_PARAM_KEY = "ITI_PUBLIC_PARAM_KEY";

    /**
     * 默认存储bucket
     */
    public static final String DEFAULT_BUCKET_NAME = "iti";

    /**
     * 异常编码版本号
     */
    public static final int EXCEPTION_ERROR_CODE_VERSION = 1;
}
