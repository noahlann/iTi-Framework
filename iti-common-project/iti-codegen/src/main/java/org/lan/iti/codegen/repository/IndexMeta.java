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

import lombok.Value;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Index元数据
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@Value
public class IndexMeta {
    boolean unique;
    List<ParamElement> params = new ArrayList<>();

    public IndexMeta(boolean unique) {
        this.unique = unique;
    }

    public void addParam(VariableElement element) {
        this.params.add(new ParamElement(element));
    }
}
