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

package org.lan.iti.cloud.security.feign;

import feign.Headers;
import org.lan.iti.cloud.security.model.OauthClientDetails;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程 OAuth 客户端获取
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
public interface RemoteClientDetailsService {

    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping("/client/details/{clientId}")
    ApiResult<OauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);
}
