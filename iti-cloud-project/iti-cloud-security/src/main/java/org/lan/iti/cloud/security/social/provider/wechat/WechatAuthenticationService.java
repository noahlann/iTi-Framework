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

package org.lan.iti.cloud.security.social.provider.wechat;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.exception.ITIAuth2Exception;
import org.lan.iti.cloud.security.feign.RemoteSocialService;
import org.lan.iti.cloud.security.model.SecuritySocialDetails;
import org.lan.iti.cloud.security.social.SocialAuthenticationToken;
import org.lan.iti.cloud.security.social.SocialConstants;
import org.lan.iti.cloud.security.social.service.AbstractSocialAuthenticationService;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.util.Formatter;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 微信登录
 *
 * @author NorthLan
 * @date 2020-05-26
 * @url https://noahlan.com
 */
@Slf4j
public class WechatAuthenticationService extends AbstractSocialAuthenticationService {

    private final RemoteSocialService remoteSocialService;

    public WechatAuthenticationService(RemoteSocialService remoteSocialService) {
        super(null);
        this.remoteSocialService = remoteSocialService;
    }

    @Override
    public String getProviderId() {
        return SocialConstants.DefaultProvider.WECHAT;
    }

    @Override
    public SocialAuthenticationToken authRequest(String domain, HttpServletRequest request, Map<String, String> parameters) {
        String code = parameters.get("code");
        return new SocialAuthenticationToken(getProviderId(), domain, code, null, parameters);
    }

    @Override
    public void authenticate(SocialAuthenticationToken authRequest) throws AuthenticationException {
        // 获取微信openid
        ApiResult<SecuritySocialDetails> socialDetailsApiResult = remoteSocialService.getByType(getProviderId());
        if (!isRemoteDataValid(socialDetailsApiResult)) {
            throw new ITIAuth2Exception("系统无法查询到第三方参数信息，请联系管理员");
        }
        SecuritySocialDetails socialDetails = socialDetailsApiResult.getData();
        String appId = socialDetails.getAppId();
        String appSecret = socialDetails.getAppSecret();
        String authorizationUrl = socialDetails.getAuthorizationUrl();
        String url = String.format(authorizationUrl, appId, appSecret, authRequest.getPrincipal().toString());
        String result = HttpUtil.get(url);
        log.debug("微信相应报文：{}", result);
        // json
        JSONObject resultJson = JSONUtil.parseObj(result);
        Integer errCode = resultJson.getInt("errcode");
        if (errCode != null) {
            throw new ITIAuth2Exception(Formatter.format("微信鉴权错误: {}，请联系管理员", resultJson.getStr("errmsg")));
        }
        authRequest.setPrincipal(resultJson.get("openid"));
    }

    private boolean isRemoteDataValid(ApiResult<SecuritySocialDetails> socialDetails) {
        if (socialDetails == null) {
            return false;
        }
        return socialDetails.getData() != null;
    }
}
