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

package org.lan.iti.common.core.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.sensitive.util.DesensitizedUtils;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * 脱敏序列化器
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveTypeEnum type;
    private Integer prefixNoMaskLen;
    private Integer suffixNoMaskLen;
    private String maskStr;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Assert.notNull(type, "Sensitive type enum should not be null.");
        // TODO 优化switch
        switch (type) {
            case CHINESE_NAME: {
                gen.writeString(DesensitizedUtils.chineseName(value));
                break;
            }
            case ID_CARD: {
                gen.writeString(DesensitizedUtils.idCardNum(value));
                break;
            }
            case FIXED_PHONE: {
                gen.writeString(DesensitizedUtils.fixedPhone(value));
                break;
            }
            case MOBILE_PHONE: {
                gen.writeString(DesensitizedUtils.mobilePhone(value));
                break;
            }
            case ADDRESS: {
                gen.writeString(DesensitizedUtils.address(value));
                break;
            }
            case EMAIL: {
                gen.writeString(DesensitizedUtils.email(value));
                break;
            }
            case BANK_CARD: {
                gen.writeString(DesensitizedUtils.bankCard(value));
                break;
            }
            case PASSWORD: {
                gen.writeString(DesensitizedUtils.password(value));
                break;
            }
            case KEY: {
                gen.writeString(DesensitizedUtils.key(value));
                break;
            }
            case CUSTOMER: {
                Assert.notNull(prefixNoMaskLen, "CUSTOMER prefixNoMaskLen should not be null.");
                Assert.notNull(suffixNoMaskLen, "CUSTOMER suffixNoMaskLen should not be null.");
                Assert.notNull(maskStr, "CUSTOMER maskStr should not be null.");
                gen.writeString(DesensitizedUtils.desValue(value, prefixNoMaskLen, suffixNoMaskLen, maskStr));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown sensitive type enum $type");
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            if (property.getType().getRawClass() == String.class) {
                Sensitive sensitive = property.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    sensitive = property.getContextAnnotation(Sensitive.class);
                }
                if (sensitive != null) {
                    return new SensitiveSerializer(sensitive.type(), sensitive.prefixNoMaskLen(), sensitive.suffixNoMaskLen(), sensitive.maskStr());
                }
            }
            return prov.findValueSerializer(property.getType(), property);
        }
        return prov.findNullValueSerializer(null);
    }
}
