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

package org.lan.iti.common.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.data.dynamic.DomainContextHolder;
import org.lan.iti.common.data.tenant.TenantContextHolder;

/**
 * 多租户增强
 * <p>
 * 内部feign访问传递租户信息
 * </p>
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
public class ITIFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (TenantContextHolder.hasTenant()) {
            requestTemplate.header(ITIConstants.TENANT_ID_HEADER_NAME, TenantContextHolder.getTenantId());
            log.debug("请求链中存在租户信息，Feign增强");
        }
        if (DomainContextHolder.hasDomain()) {
            requestTemplate.header(ITIConstants.DOMAIN_HEADER_NAME, DomainContextHolder.getDomain());
            log.debug("请求链中存在域(domain)信息，Feign增强");
        }
    }
}
