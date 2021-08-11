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

package org.lan.iti.iha.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.lan.iti.iha.core.model.BasicCredentials;
import org.lan.iti.iha.oauth2.enums.ClientAuthenticationMethod;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientCertificate {
    private String id;
    private String secret;
    private String method;

    public ClientCertificate(BasicCredentials basicCredentials) {
        this.id = basicCredentials.getId();
        this.secret = basicCredentials.getSecret();
        this.method = ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getMethod();
    }
}
