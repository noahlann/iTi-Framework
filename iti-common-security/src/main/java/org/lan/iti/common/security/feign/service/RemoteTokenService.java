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

package org.lan.iti.common.security.feign.service;

import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程token调用
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public interface RemoteTokenService {

    /**
     * 分页查询 token 信息
     */
    @GetMapping(value = "/token/page",
            headers = SecurityConstants.HEADER_FROM_IN)
    ApiResult<Object> pageToken(/* TODO PageQuery */);

    /**
     * 删除 token
     *
     * @param token token
     */
    @DeleteMapping(value = "/token/{token}",
            headers = SecurityConstants.HEADER_FROM_IN)
    ApiResult<Boolean> removeTokenById(@PathVariable("token") String token);
}
