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

import org.lan.iti.codegen.creator.GenCreatorPlugin;
import org.lan.iti.codegen.repository.GenDomainRepositoryPlugin;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * APT处理器,引入静态插件机制
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NLDDDProcessor extends AbstractGeneratorProcessor {
    public static final ProcessorPlugin[] PLUGINS = new ProcessorPlugin[]{
            new GenCreatorPlugin(),
            new GenDomainRepositoryPlugin()
    };

    @Override
    protected ProcessorPlugin[] getPlugins() {
        return PLUGINS;
    }
}
