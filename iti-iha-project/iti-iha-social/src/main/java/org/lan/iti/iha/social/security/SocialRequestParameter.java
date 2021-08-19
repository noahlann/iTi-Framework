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

package org.lan.iti.iha.social.security;

import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessorType;
import org.lan.iti.iha.social.SocialConfig;

/**
 * @author NorthLan
 * @date 2021/8/14
 * @url https://blog.noahlan.com
 */
public class SocialRequestParameter extends RequestParameter {
    private static final long serialVersionUID = 7138651785484433437L;
    public static final String KEY_CONFIG = "$config.social";

    public SocialRequestParameter(RequestParameter parameter) {
        super(parameter);
        initProcessorType();
    }

    private void initProcessorType() {
        setProcessorType(ProcessorType.SOCIAL.getCode());
    }

    public SocialRequestParameter setConfig(SocialConfig config) {
        this.put(KEY_CONFIG, config);
        return this;
    }

    public SocialConfig getConfig() {
        return getByKey(KEY_CONFIG);
    }
}
