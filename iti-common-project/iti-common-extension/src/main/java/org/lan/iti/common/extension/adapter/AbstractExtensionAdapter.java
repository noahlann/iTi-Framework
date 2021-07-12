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

package org.lan.iti.common.extension.adapter;

import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.lan.iti.common.extension.support.NamedClassCache;

import java.util.function.Function;

/**
 * @author NorthLan
 * @date 2021-07-09
 * @url https://noahlan.com
 */
public abstract class AbstractExtensionAdapter implements ExtensionAdapter {
    protected final Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClass;
    protected final Function<Class<?>, NamedClassCache<Object>> getExtensionInstances;
    protected boolean loaded = false;

    public AbstractExtensionAdapter(Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClasses,
                                    Function<Class<?>, NamedClassCache<Object>> getExtensionInstances) {
        this.getExtensionClass = getExtensionClasses;
        this.getExtensionInstances = getExtensionInstances;
    }

    public void setParameter(ExtensionAdapterParameter parameter) {
        if (this.loaded) {
            return;
        }
        this.loadParameter(parameter);
        this.loaded = true;
    }

    public void load() {
        if (matches(null)) {
            this.init();
            this.reset();
        }
    }

    protected abstract void loadParameter(ExtensionAdapterParameter parameter);

    protected abstract void reset();
}
