/*
 * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.cloud.mock.interfaces.api.rest;

import lombok.AllArgsConstructor;
import org.lan.iti.cloud.mock.application.service.UserApplicationService;
import org.lan.iti.cloud.mock.interfaces.dto.UserRegisterRequest;
import org.lan.iti.common.core.api.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户
 *
 * @author NorthLan
 * @date 2021-02-04
 * @url https://noahlan.com
 */
@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    public ApiResult<?> register(UserRegisterRequest userRegisterRequest) {
        userApplicationService.register(userRegisterRequest);
        return ApiResult.ok();
    }
}
