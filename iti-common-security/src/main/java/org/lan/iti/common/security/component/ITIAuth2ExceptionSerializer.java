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
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.enums.ErrorTypeEnum;
import org.lan.iti.common.core.error.ErrorCode;
import org.lan.iti.common.core.properties.ErrorCodeProperties;
import org.lan.iti.common.core.util.SpringContextHolder;
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
@Slf4j
public class ITIAuth2ExceptionSerializer extends StdSerializer<ITIAuth2Exception> {
    private static final long serialVersionUID = 5490119983903709414L;

    protected ITIAuth2ExceptionSerializer() {
        super(ITIAuth2Exception.class);
    }

    @Override
    public void serialize(ITIAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // status errorCode msg data -> ApiResult
        // 获取配置
        ErrorCodeProperties properties = SpringContextHolder.getBeanOfNull(ErrorCodeProperties.class);
        if (properties == null) {
            log.warn("无法读取异常配置[{}],将不再按照异常规范返回", ErrorCodeProperties.PREFIX);
            gen.writeObject(ApiResult.error(value.getErrorCode(), value.getMessage()));
        } else {
            if (properties.isEnabled()) {
                ErrorCode errorCode = ErrorCode.builder()
                        .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                        .mark(properties.getMark())
                        .type(ErrorTypeEnum.FRAMEWORK.getValue())
                        .level(ErrorLevelEnum.PRIMARY.getValue())
                        .code(0)
                        .build();
                gen.writeObject(ApiResult.error(errorCode.toString(), value.getErrorCode() + "|" + value.getMessage()));
            } else {
                gen.writeObject(ApiResult.error(value.getErrorCode(), value.getMessage()));
            }
        }
    }
}
