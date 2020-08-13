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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import feign.Headers;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.model.page.PageParameter;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 远程token调用
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public interface RemoteTokenService {

    /**
     * 获取当前所有缓存的token信息
     *
     * @return Token信息列表
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping(value = "/token/list")
    List<OAuth2AccessToken> tokenList();

    /**
     * 获取在线的userId-tokens列表
     *
     * @return userId:[tokens] map
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping(value = "/token/user/list")
    Map<String, List<String>> userIdList();

    /**
     * 删除 token
     *
     * @param token token
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @DeleteMapping(value = "/token/{token}")
    Boolean delToken(@PathVariable("token") String token);

    /**
     * 更新token数据,包括UserDetails
     *
     * @param token        此用户的token
     * @param securityUser 此用户的新信息
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @PutMapping(value = "/token/{token}")
    void updateToken(@PathVariable("token") String token,
                     @RequestBody SecurityUser<?> securityUser);

    /**
     * 分页查询 token 信息
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping(value = "/token/page")
    Page<OAuth2AccessToken> tokenPage(PageParameter parameter);

    //-------------------------------//
    //---------TokenEndpoint---------//
    //-------------------------------//

//    /**
//     * 获取 token
//     *
//     * @param parameters 参数
//     * @return token
//     */
//    @GetMapping(value = "/oauth/token")
//    ResponseEntity<OAuth2AccessToken> getAccessToken(@RequestParam Map<String, String> parameters);
//
//    @PostMapping(value = "/oauth/token")
//    ResponseEntity<OAuth2AccessToken> postAccessToken(@RequestParam Map<String, String> parameters);
}
