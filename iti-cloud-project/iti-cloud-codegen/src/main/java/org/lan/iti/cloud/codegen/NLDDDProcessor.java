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

package org.lan.iti.cloud.codegen;

import com.google.auto.service.AutoService;
import org.lan.iti.cloud.codegen.converter.GenJpaEnumConverterPlugin;
import org.lan.iti.cloud.codegen.creator.GenCreatorPlugin;
import org.lan.iti.cloud.codegen.repository.domain.GenDomainRepositoryPlugin;
import org.lan.iti.cloud.codegen.repository.jpa.GenJpaRepositoryPlugin;
import org.lan.iti.codegen.AbstractCodeGenProcessor;
import org.lan.iti.codegen.ProcessorPlugin;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * @author NorthLan
 * @date 2021-03-06
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NLDDDProcessor extends AbstractCodeGenProcessor {
    public final ProcessorPlugin[] PLUGINS = new ProcessorPlugin[]{
            new GenCreatorPlugin(),
            new GenDomainRepositoryPlugin(),
            new GenJpaRepositoryPlugin(),
            new GenJpaEnumConverterPlugin()};

    @Override
    protected ProcessorPlugin[] getPlugins() {
        return PLUGINS;
    }
}
