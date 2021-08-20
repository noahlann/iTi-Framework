///*
// *
// *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.cloud.security.authority.vote;
//
//import cn.hutool.core.util.StrUtil;
//import org.lan.iti.common.core.constants.SecurityConstants;
//import org.springframework.security.access.AccessDecisionVoter;
//import org.springframework.security.access.ConfigAttribute;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.FilterInvocation;
//
//import java.util.Collection;
//
///**
// * iTi授权投票器
// * 仅处理资源代码以指定前缀开始的资源
// *
// * @author NorthLan
// * @date 2020-05-09
// * @url https://noahlan.com
// */
//public class ITIAuthorityVoter implements AccessDecisionVoter<FilterInvocation> {
//    @Override
//    public boolean supports(ConfigAttribute attribute) {
//        return attribute != null
//                && attribute.getAttribute() != null
//                && attribute.getAttribute().startsWith(SecurityConstants.PREFIX_AUZ);
//    }
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return FilterInvocation.class.isAssignableFrom(clazz);
//    }
//
//    @Override
//    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> attributes) {
//        if (authentication == null) {
//            return ACCESS_DENIED;
//        }
//        int result = ACCESS_ABSTAIN; // 弃权
//        Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);
//        for (ConfigAttribute attribute : attributes) {
//            if (this.supports(attribute)) {
//                result = ACCESS_DENIED; // 拒绝
//
//                // Attempt to find a matching granted authority
//                for (GrantedAuthority authority : authorities) {
//                    if (StrUtil.equals(attribute.getAttribute(), authority.getAuthority())) {
//                        return ACCESS_GRANTED;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    protected Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
//        return authentication.getAuthorities();
//    }
//}
