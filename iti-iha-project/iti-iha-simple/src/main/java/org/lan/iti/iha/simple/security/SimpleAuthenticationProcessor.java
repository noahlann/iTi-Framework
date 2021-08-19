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

package org.lan.iti.iha.simple.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.exception.authentication.UnknownAccountException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.userdetails.UserDetails;

/**
 * 3. SimpleAuthenticationProcessor (principal instanceof UserDetails)
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@AllArgsConstructor
@Data
public class SimpleAuthenticationProcessor extends AbstractSimpleAuthenticationProcessor {

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication instanceof SimpleAuthenticationToken &&
                parameter instanceof SimpleRequestParameter;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        return process((SimpleRequestParameter) parameter, (SimpleAuthenticationToken) authentication, chain);
    }

    private Authentication process(SimpleRequestParameter parameter, SimpleAuthenticationToken authentication, ProcessChain chain) throws AuthenticationException {
        if (authentication.getPrincipal() instanceof UserDetails) {
            return chain.process(parameter,
                    createSuccessAuthentication((UserDetails) authentication.getPrincipal(), authentication));
        }
        UserDetails userDetails;
        try {
            userDetails = IhaSecurity.getContext().getUserDetailsService()
                    .loadByType(authentication.getPrincipal(), authentication.getType(), null);
        } catch (UnknownAccountException e) {
            throw new UnknownAccountException(String.format("Authenticate[%s] user [%s] not found", authentication.getType(), authentication.getPrincipal()));
        }
        if (userDetails == null) {
            throw new UnknownAccountException(String.format("Authenticate[%s] user [%s] not found", authentication.getType(), authentication.getPrincipal()));
        }
        IhaSecurity.getContext().getAuthenticationChecker().check(userDetails, authentication);
        return chain.process(parameter, createSuccessAuthentication(userDetails, authentication));
    }

    private Authentication createSuccessAuthentication(UserDetails userDetails,
                                                       SimpleAuthenticationToken authentication) {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        SimpleAuthenticationToken result = new SimpleAuthenticationToken(userDetails,
                authentication.getCredentials(), authentication.getType(), userDetails.getAuthorities());
        result.setRememberMe(authentication.isRememberMe())
                .setCode(authentication.getCode())
                .setExtra(authentication.getExtra());
        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user");
        return result;
    }
}
