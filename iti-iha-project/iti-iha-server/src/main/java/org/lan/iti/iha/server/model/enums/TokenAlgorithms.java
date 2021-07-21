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

package org.lan.iti.iha.server.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jose4j.keys.EcKeyUtil;
import org.jose4j.keys.RsaKeyUtil;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TokenAlgorithms {
    NONE("none", "none"),
    RS256("RS256", RsaKeyUtil.RSA),
    RS384("RS384", RsaKeyUtil.RSA),
    RS512("RS512", RsaKeyUtil.RSA),
    ES256("ES256", EcKeyUtil.EC),
    ES384("ES384", EcKeyUtil.EC),
    ES512("ES512", EcKeyUtil.EC),
    ;

    private final String alg;
    private final String keyType;
}
