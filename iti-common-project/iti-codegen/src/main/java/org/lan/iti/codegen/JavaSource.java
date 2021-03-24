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

package org.lan.iti.codegen;

import cn.hutool.core.collection.CollUtil;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@Value
public class JavaSource {
    Set<String> fields = new HashSet<>();
    Set<String> methods = new HashSet<>();
    String pkgName;
    String clsName;

    @Getter(AccessLevel.PRIVATE)
    TypeSpec.Builder typeSpecBuilder;

    public String getFullName() {
        return pkgName + "." + clsName;
    }

    public TypeSpec getTypeSpec() {
        return getTypeSpecBuilder().build();
    }

    public void addMethod(MethodSpec method) {
        String key = createMethodKey(method);
        if (this.methods.contains(key)) {
            throw new RuntimeException(String.format("Repetition Method %s", key));
        }
        this.typeSpecBuilder.addMethod(method);
        this.methods.add(key);
    }

    public void addField(FieldSpec fieldSpec) {
        String key = fieldSpec.name;
        if (this.fields.contains(key)) {
            throw new RuntimeException(String.format("Repetition Field %s", key));
        }
        this.typeSpecBuilder.addField(fieldSpec);
        this.fields.add(key);
    }

    public boolean hasField(String field) {
        return fields.contains(field);
    }

    private String createMethodKey(MethodSpec method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.name);
        if (CollUtil.isEmpty(method.parameters)) {
            stringBuilder.append("()");
        } else {
            stringBuilder.append("(");
            stringBuilder.append(method.parameters.stream()
                    .map(parameterSpec -> parameterSpec.type)
                    .map(TypeName::toString)
                    .collect(Collectors.joining(","))
            );
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    public void addType(TypeSpec type) {
        this.typeSpecBuilder.addType(type);
    }
}
