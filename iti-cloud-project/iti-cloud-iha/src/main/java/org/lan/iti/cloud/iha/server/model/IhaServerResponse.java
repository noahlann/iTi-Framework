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

package org.lan.iti.cloud.iha.server.model;

import cn.hutool.core.util.ObjectUtil;
import org.lan.iti.cloud.iha.server.model.enums.ErrorResponse;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class IhaServerResponse<K, V> extends HashMap<String, Object> {
    private static final long serialVersionUID = 5584929345166246094L;

    private final static String ERROR = "error";
    private final static String ERROR_DESCRIPTION = "errorDescription";
    private final static String ERROR_URI = "error_uri";
    private final static String STATE = "state";
    private final static String DATA = "data";

    public IhaServerResponse<K, V> error(ErrorResponse errorCode) {
        return this.error(errorCode.getError())
                .errorDescription(errorCode.getErrorDescription());
    }

    public IhaServerResponse<K, V> error(String errorCode) {
        this.put(ERROR, errorCode);
        return this;
    }

    public IhaServerResponse<K, V> errorDescription(String errorDescription) {
        this.put(ERROR_DESCRIPTION, errorDescription);
        return this;
    }

    public IhaServerResponse<K, V> errorUri(String errorUri) {
        this.put(ERROR_URI, errorUri);
        return this;
    }

    public IhaServerResponse<K, V> state(String state) {
        this.put(STATE, state);
        return this;
    }

    public IhaServerResponse<K, V> data(Object data) {
        this.put(ERROR, "");
        this.put(ERROR_DESCRIPTION, "");
        this.put(DATA, data);
        return this;
    }

    public boolean isSuccess() {
        return StringUtil.isEmpty(this.getError());
    }

    public IhaServerResponse<K, V> add(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public IhaServerResponse<K, V> addAll(Map<String, Object> map) {
        this.putAll(map);
        return this;
    }

    public String getError() {
        return ObjectUtil.isEmpty(this.get(ERROR)) ? null : String.valueOf(this.get(ERROR));
    }

    public String getErrorDescription() {
        return ObjectUtil.isEmpty(this.get(ERROR_DESCRIPTION)) ? null : String.valueOf(this.get(ERROR_DESCRIPTION));
    }

    public String getErrorUri() {
        return ObjectUtil.isEmpty(this.get(ERROR_URI)) ? null : String.valueOf(this.get(ERROR_URI));
    }

    public String getState() {
        return ObjectUtil.isEmpty(this.get(STATE)) ? null : String.valueOf(this.get(STATE));
    }

    public V getData() {
        return (V) this.get(DATA);
    }
}
