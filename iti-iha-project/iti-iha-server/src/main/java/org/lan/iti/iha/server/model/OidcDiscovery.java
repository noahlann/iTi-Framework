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

package org.lan.iti.iha.server.model;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
public class OidcDiscovery implements Serializable {
    private static final long serialVersionUID = 5049173258659877405L;

    protected String issuer;
    protected String authorization_endpoint;
    protected String token_endpoint;
    protected String userinfo_endpoint;
    protected String registration_endpoint;
    protected String end_session_endpoint;
    protected String check_session_iframe;
    protected String jwks_uri;
    protected List<String> grant_types_supported;
    protected List<String> response_modes_supported;
    protected List<String> response_types_supported;
    protected List<String> scopes_supported;
    protected List<String> token_endpoint_auth_methods_supported;
    protected List<String> request_object_signing_alg_values_supported;
    protected List<String> userinfo_signing_alg_values_supported;
    protected boolean request_parameter_supported;
    protected boolean request_uri_parameter_supported;
    protected boolean require_request_uri_registration;
    protected boolean claims_parameter_supported;
    protected List<String> id_token_signing_alg_values_supported;
    protected List<String> subject_types_supported;
    protected List<String> claims_supported;
    protected List<String> code_challenge_methods_supported;
}
