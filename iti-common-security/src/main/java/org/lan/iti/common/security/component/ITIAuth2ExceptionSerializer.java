/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.exception.ITIAuth2Exception;

import java.io.IOException;

/**
 * 自定义OAuth2异常序列化器
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
public class ITIAuth2ExceptionSerializer extends StdSerializer<ITIAuth2Exception> {
    private static final long serialVersionUID = 5490119983903709414L;

    protected ITIAuth2ExceptionSerializer() {
        super(ITIAuth2Exception.class);
    }

    @Override
    public void serialize(ITIAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // status errorCode msg data -> ApiResult
        gen.writeObject(ApiResult.error(value.getErrorCode(), value.getMessage()));
    }
}
