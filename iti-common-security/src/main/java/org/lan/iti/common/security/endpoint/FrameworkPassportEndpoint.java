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

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.exception.ServiceException;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.annotation.Inner;
import org.lan.iti.common.security.endpoint.constants.PassportConstants;
import org.lan.iti.common.security.endpoint.service.PassportService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 身份认证端点
 *
 * @author NorthLan
 * @date 2020-11-06
 * @url https://noahlan.com
 */
@Slf4j
@RestController
@RequestMapping("passport")
@AllArgsConstructor
public class FrameworkPassportEndpoint {
    private final Map<String, PassportService> passportServiceMap;

    @GetMapping("login")
    @Inner(false)
    public ApiResult<?> getLogin(@RequestParam Map<String, String> params) {
        return postLogin(params);
    }

    @PostMapping("login")
    @Inner(false)
    public ApiResult<?> postLogin(@RequestParam Map<String, String> params) {
        String grantType = params.get(PassportConstants.GRANT_TYPE);
        if (StrUtil.isBlank(grantType)) {
            throw new ServiceException(1000, "未指定 grant_type,请检查！");
        }
        PassportService passportService = passportServiceMap.get(PassportConstants.PREFIX_PASSPORT_SERVICE + grantType);
        if (passportService == null) {
            throw new ServiceException(1001, Formatter.format("系统未实现类型为:[{}] 的登录方式,请检查！", grantType));
        }
        return ApiResult.ok(passportService.grant(params));
    }

    /**
     * 刷新当前用户Token
     *
     * @return 新token
     */
    @PostMapping("refresh")
    public ApiResult<?> refreshToken() {
        PassportService passportService = null;
        for (Map.Entry<String, PassportService> entry : passportServiceMap.entrySet()) {
            passportService = entry.getValue();
            break;
        }
        if (passportService == null) {
            throw new ServiceException(1001, "系统未实现任何登录类型");
        }
        return ApiResult.ok(passportService.refreshToken());
    }
}
