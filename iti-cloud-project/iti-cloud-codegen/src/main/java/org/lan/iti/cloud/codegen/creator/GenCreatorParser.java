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

package org.lan.iti.cloud.codegen.creator;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * Parser
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Getter
public class GenCreatorParser {
    private final TypeElement typeElement;
    private String packageName;
    private String className;
    private String parentClassName;
    private boolean genClass;

    public GenCreatorParser(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public void read(Annotation annotation) {
        if (annotation instanceof GenCreator) {
            readFromGenCreator((GenCreator) annotation);
        }
    }

    private void readFromGenCreator(GenCreator genCreator) {
        this.genClass = genCreator.genClass();
        this.packageName = typeElement.getEnclosingElement().toString();
        this.className = typeElement.getSimpleName().toString() + "Creator";
        this.parentClassName = "";
        if (!this.genClass) {
            this.className = "Base" + this.className;
        }
        if (StrUtil.isNotEmpty(genCreator.parent())) {
            this.parentClassName = genCreator.parent();
        }
    }
}
