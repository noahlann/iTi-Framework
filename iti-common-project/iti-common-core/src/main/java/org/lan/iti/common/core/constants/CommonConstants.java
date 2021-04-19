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
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
public interface CommonConstants {

    /**
     * 框架名
     */
    String FRAMEWORK_NAME = "iTi-Framework";

    // region Codes
    /**
     * 编码 code
     */
    String CODE_CODE = "code";

    /**
     * 消息 message
     */
    String CODE_MESSAGE = "message";

    /**
     * 状态 status
     */
    String CODE_STATUS = "status";

    /**
     * 错误 error
     */
    String CODE_ERROR = "error";
    // endregion

    // region Header name
    /**
     * 请求号header标识
     */
    String REQUEST_NO_HEADER_NAME = "Request-No";

    /**
     * header中的spanId，传递规则: request header中传递本服务的id
     */
    String SPAN_ID_HEADER_NAME = "Span-Id";

    /**
     * header 中租户ID
     */
    String TENANT_ID_HEADER_NAME = "TENANT_ID";

    /**
     * header中的domain信息
     */
    String DOMAIN_HEADER_NAME = "domain";

    /**
     * Feign 传递泛型的实际类名
     */
    String FEIGN_GENERICS_HEADER_NAME = "ITI-FEIGN-GENERICS";

    /**
     * 前缀key
     */
    String PREFIX_KEY = "prefix";

    /**
     * header 中版本信息
     */
    String VERSION = "VER";
    // endregion

    // region Defaults
    /**
     * 系统默认ID
     */
    String DEFAULT_ID = "0";

    /**
     * 默认租户ID = 0
     */
    String DEFAULT_TENANT_ID = DEFAULT_ID;

    /**
     * 默认消息源
     */
    String DEFAULT_MESSAGES = "classpath:i18n/messages";

    /**
     * 默认数据验证消息源
     */
    String DEFAULT_VALIDATOR_MESSAGES = "classpath:i18n/validation/ValidationMessages";
    // endregion

    // region Bean name
    /**
     * iTi 默认消息源 bean 名称
     */
    String MESSAGE_SOURCE_BEAN_NAME = "itiMessageSource";

    /**
     * 参数验证消息源bean名称
     */
    String VALIDATION_MESSAGE_SOURCE_BEAN_NAME = "validationMessageSource";
    // endregion

    /**
     * 编码
     */
    String UTF_8 = "UTF-8";

    /**
     * 树 根节点ID
     */
    String TREE_ROOT_ID = "-1";
}
