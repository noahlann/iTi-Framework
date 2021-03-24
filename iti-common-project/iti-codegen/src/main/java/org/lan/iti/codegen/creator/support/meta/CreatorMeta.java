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

package org.lan.iti.codegen.creator.support.meta;

import org.lan.iti.codegen.support.AccessLevel;
import org.lan.iti.codegen.support.meta.AbstractModelMeta;

import javax.lang.model.element.TypeElement;

/**
 * CreatorMeta
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public class CreatorMeta extends AbstractModelMeta<CreatorSetterMeta> {
    public CreatorMeta(TypeElement typeElement) {
        super(typeElement);
    }

    @Override
    protected void merge(CreatorSetterMeta s, CreatorSetterMeta n) {
        s.merge(n);
    }

    @Override
    protected boolean accept(CreatorSetterMeta creatorSetterMeta) {
        return creatorSetterMeta.accessLevel() == AccessLevel.PUBLIC
                || creatorSetterMeta.accessLevel() == AccessLevel.PROTECTED
                || creatorSetterMeta.accessLevel() == AccessLevel.PACKAGE;
    }
}
