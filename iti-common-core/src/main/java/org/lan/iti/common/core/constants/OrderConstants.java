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
 * 框架AOP-Order常量
 * <p>
 * 数值越低越靠前
 * </p>
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
public final class OrderConstants {
    /**
     * GlobalExceptionHandler
     */
    public static final int GLOBAL_EXCEPTION_HANDLER = 200;

    /**
     * SQLExceptionHandler
     */
    public static final int SQL_EXCEPTION_HANDLER = GLOBAL_EXCEPTION_HANDLER - 1;

    /**
     * 临时保存RequestData的aop
     */
    public static final int REQUEST_DATA_AOP_SORT = 500;

    /**
     * 参数校验为空的aop
     */
    public static final int PARAM_VALIDATE_AOP_SORT = 510;

    /**
     * 控制器调用链的aop
     */
    public static final int CHAIN_ON_CONTROLLER_SORT = 600;

    /**
     * provider的调用链aop
     */
    public static final int CHAIN_ON_PROVIDER_SORT = 610;

    /**
     * consumer的调用链aop
     */
    public static final int CHAIN_ON_CONSUMMER_SORT = 620;
}
