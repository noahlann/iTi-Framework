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

package org.lan.iti.cloud.codegen.repository.domain;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;

import javax.lang.model.element.VariableElement;

/**
 * 参数元素元数据
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@Getter
public class ParamElement {
    private final boolean field;
    private final TypeName typeName;
    private final String name;

    ParamElement(VariableElement element) {
        this.typeName = TypeName.get(element.asType());
        this.name = element.getSimpleName().toString();
        this.field = true;
    }

    ParamElement(Class<?> type, String name) {
        this.typeName = ClassName.get(type);
        this.name = name;
        this.field = false;
    }
}
