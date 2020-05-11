/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.lan.iti.common.core.util.LambdaUtils;
import org.lan.iti.common.security.model.BaseAuthority;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * BaseGrantedAuthority 反序列化
 *
 * @author NorthLan
 * @date 2020-05-11
 * @url https://noahlan.com
 */
public class SecurityUserDeserializer extends JsonDeserializer<SecurityUser<?>> {

    @Override
    public SecurityUser<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode jsonNode = mapper.readTree(p);

        List<? extends GrantedAuthority> authorities =
                mapper.convertValue(
                        readJsonNode(jsonNode, LambdaUtils.getFieldName(SecurityUser<Object>::getAuthorities)),
                        new TypeReference<List<BaseAuthority>>() {
                        });
        List<String> roles =
                mapper.convertValue(readJsonNode(jsonNode, LambdaUtils.getFieldName(SecurityUser<Object>::getRoles)),
                        new TypeReference<List<String>>() {
                        });
        Object accountInfo = mapper.convertValue(readJsonNode(jsonNode, LambdaUtils.getFieldName(SecurityUser<Object>::getAccountInfo)), Map.class);

        String userId = readJsonNode(jsonNode, LambdaUtils.getFieldName(SecurityUser<Object>::getUserId)).asText();

        return new SecurityUser<>(accountInfo, userId, authorities, roles);
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
