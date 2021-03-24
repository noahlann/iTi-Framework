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

package org.lan.iti.codegen.repository;

import com.squareup.javapoet.TypeSpec;
import org.lan.iti.codegen.AbstractProcessorPlugin;
import org.lan.iti.codegen.JavaSource;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * GenDomainRepository 插件
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
public class GenDomainRepositoryPlugin extends AbstractProcessorPlugin {
    @Override
    protected void process(TypeElement typeElement, Annotation t) {
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            return;
        }
        GenDomainRepositoryMetaParser parser = new GenDomainRepositoryMetaParser(getTypeCollector());
        GenDomainRepositoryMeta meta = parser.parse(typeElement, t);

        GenDomainRepositoryBuilderFactory factory = new GenDomainRepositoryBuilderFactory(meta);
        TypeSpec.Builder builder = factory.create();

        // writer

        getJavaSourceCollector().register(new JavaSource(meta.getPkgName(), meta.getClsName(), builder));
    }

    @Override
    public <A extends Annotation> Class<A>[] applyAnnCls() {
        return new Class[]{GenDomainRepository.class};
    }

    @Override
    public <A extends Annotation> Class<A>[] ignoreAnnCls() {
        return new Class[0];
    }
}
