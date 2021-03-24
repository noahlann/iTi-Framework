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

package org.lan.iti.cloud.mock.application.service;

import lombok.AllArgsConstructor;
import org.lan.iti.cloud.mock.domain.service.UserDomainService;
import org.lan.iti.cloud.mock.interfaces.dto.UserRegisterRequest;
import org.springframework.stereotype.Service;

/**
 * 用户 应用服务
 *
 * @author NorthLan
 * @date 2021-02-04
 * @url https://noahlan.com
 */
@Service
@AllArgsConstructor
public class UserApplicationService {
    private final UserDomainService userDomainService;

    public void register(UserRegisterRequest request) {
        // TODO po -> vo
    }
}
