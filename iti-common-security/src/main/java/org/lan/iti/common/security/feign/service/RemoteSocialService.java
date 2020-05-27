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

package org.lan.iti.common.security.feign.service;

import feign.Headers;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.model.SecuritySocialDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程第三方信息服务接口
 *
 * @author NorthLan
 * @date 2020-05-26
 * @url https://noahlan.com
 */
public interface RemoteSocialService {

    /**
     * 获取系统第三方信息
     *
     * @param type 类型
     * @return 信息
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping(value = "security/social/details")
    ApiResult<SecuritySocialDetails> getByType(@RequestParam("type") String type);
}
