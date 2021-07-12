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

package org.lan.iti.cloud.iha.mfa;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * Multi-Factor Authenticator Configuration
 *
 * @author NorthLan
 * @date 2021-07-12
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaMfaConfig {
    /**
     * the number of digits in the generated code.
     */
    private int digits = 6;
    /**
     * the time step size, in milliseconds, as specified by RFC 6238. The default value is 30.000s.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6238#section-5.2" target="_blank">5.2.  Validation and Time-Step Size</a>
     */
    private long period = 30000;
    /**
     * the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     */
    private IhaMfaAlgorithm algorithm = IhaMfaAlgorithm.HmacSHA1;

    /**
     * temp path
     * <p>
     * ${user.home}\iha\temp
     */
    private String qrcodeTempPath = System.getProperties().getProperty("user.home") + File.separator + "iha" + File.separator + "temp";

    private int qrcodeWidth = 200;

    private int qrcodeHeight = 200;

    private String qrcodeImgType = "gif";
}
