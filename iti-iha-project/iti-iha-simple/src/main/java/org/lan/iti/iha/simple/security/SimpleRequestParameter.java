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

import cn.hutool.core.lang.Assert;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.simple.IhaSimple;
import org.lan.iti.iha.simple.SimpleConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SimpleParameter
 *
 * @author NorthLan
 * @date 2021/8/5
 * @url https://blog.noahlan.com
 */
public class SimpleRequestParameter extends RequestParameter {
    private static final long serialVersionUID = -3270612820709182141L;
    public static final String KEY_CONFIG = "$simpleConfig";

    public SimpleRequestParameter() {
    }

    public SimpleRequestParameter(HttpServletRequest request) {
        super(request);
    }

    public SimpleRequestParameter(RequestParameter parameter) {
        super(parameter);
    }

    public SimpleRequestParameter(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public SimpleRequestParameter setSimpleConfig(SimpleConfig config) {
        Assert.notNull(config, "config cannot null");
        put(KEY_CONFIG, config);
        return this;
    }

    public SimpleConfig getSimpleConfig() {
        return getByKey(KEY_CONFIG, IhaSimple.getContext().getConfig());
    }
}
