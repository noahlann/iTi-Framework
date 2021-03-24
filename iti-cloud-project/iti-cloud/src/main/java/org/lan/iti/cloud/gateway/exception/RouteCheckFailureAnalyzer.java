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

package org.lan.iti.cloud.gateway.exception;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * 格式化路由检查异常信息，方便启动观察
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
public class RouteCheckFailureAnalyzer extends AbstractFailureAnalyzer<RouteCheckException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, RouteCheckException cause) {
        return new FailureAnalysis(cause.getMessage(), "稍后填写文案", cause);
    }
}
