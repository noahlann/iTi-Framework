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

package org.lan.iti.cloud.codegen.creator.support.parser;

import org.lan.iti.cloud.codegen.creator.GenCreatorIgnore;
import org.lan.iti.cloud.codegen.creator.support.meta.CreatorSetterMeta;
import org.lan.iti.codegen.support.parser.ModelMethodMetaParser;

import javax.lang.model.element.Element;

/**
 * CreatorSetterMeta 解析器接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public interface CreatorSetterMetaParser extends ModelMethodMetaParser<CreatorSetterMeta> {

    /**
     * 新建CreatorSetterMeta,判断忽略情况
     *
     * @param element Element
     * @return CreatorSetterMeta
     */
    default CreatorSetterMeta metaOf(Element element) {
        CreatorSetterMeta meta = new CreatorSetterMeta();
        GenCreatorIgnore ignore = element.getAnnotation(GenCreatorIgnore.class);
        if (ignore != null) {
            meta.setIgnore(true);
        }
        return meta;
    }
}
