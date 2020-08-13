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

package org.lan.iti.common.security.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 安全端点
 *
 * @author NorthLan
 * @date 2020-08-03
 * @url https://noahlan.com
 */
@Slf4j
@RestController
public class SecurityEndpoint {

    @GetMapping("/login")
    public ApiResult<?> getLogin(@RequestParam Map<String, String> parameters) {
        return postLogin(parameters);
    }

    @PostMapping("/login")
    public ApiResult<?> postLogin(@RequestParam Map<String, String> parameters) {
        OAuth2RestTemplate template;
//        template.getAccessToken()
        return ApiResult.ok();
    }
}
