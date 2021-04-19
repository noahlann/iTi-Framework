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

package org.lan.iti.cloud.codegen.repository.jpa;

import com.squareup.javapoet.TypeSpec;
import org.lan.iti.cloud.codegen.repository.jpa.writer.*;
import org.lan.iti.codegen.AbstractProcessorPlugin;
import org.lan.iti.codegen.JavaSource;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * GenJpaRepository插件
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
public class GenJpaRepositoryPlugin extends AbstractProcessorPlugin {
    @Override
    protected void process(PackageElement packageElement, Annotation t) throws Exception {
        processInner(packageElement, t);
    }

    @Override
    protected void process(TypeElement typeElement, Annotation t) throws Exception {
        processInner(typeElement, t);
    }

    private void processInner(Element element, Annotation t) throws Exception {
        if (t instanceof GenJpaRepositories) {
            for (GenJpaRepository genJpaRepository : ((GenJpaRepositories) t).value()) {
                GenJpaRepositoryMetaParser parser = new GenJpaRepositoryMetaParser(getTypeCollector(), getTypeUtils(), getElementUtils());
                GenJpaRepositoryMeta meta = parser.parse(element, genJpaRepository);

                processMeta(meta);
            }
        } else {
            GenJpaRepositoryMetaParser parser = new GenJpaRepositoryMetaParser(getTypeCollector(), getTypeUtils(), getElementUtils());
            GenJpaRepositoryMeta meta = parser.parse(element, t);

            processMeta(meta);
        }
    }

    private void processMeta(GenJpaRepositoryMeta meta) {
        GenJpaRepositoryBuilderFactory factory = new GenJpaRepositoryBuilderFactory(meta);
        TypeSpec.Builder builder = factory.create();

        JavaSource javaSource = new JavaSource(meta.getPkgName(), meta.getClsName(), builder);
        new GetByIdMethodWriter(meta).writeTo(javaSource);
        new GetAllByIdMethodWriter(meta).writeTo(javaSource);
        new RemoveByIdMethodWriter(meta).writeTo(javaSource);
        new RemoveMethodWriter(meta).writeTo(javaSource);
        new SaveMethodWriter(meta).writeTo(javaSource);
        new ExistsMethodWriter(meta).writeTo(javaSource);
        new UpdateMethodWriter(meta).writeTo(javaSource);

        getJavaSourceCollector().register(javaSource);
    }

    @Override
    public <A extends Annotation> Class<A>[] applyAnnCls() {
        return new Class[]{GenJpaRepository.class, GenJpaRepositories.class};
    }

    @Override
    public <A extends Annotation> Class<A>[] ignoreAnnCls() {
        return new Class[0];
    }
}
