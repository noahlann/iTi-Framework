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

package org.lan.iti.iha.core.model;

import cn.hutool.core.lang.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Mapped generic type
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public abstract class Mapped<Children extends Mapped<Children>> extends HashMap<String, Object> {
    private static final long serialVersionUID = 2870321854370298093L;

    public Mapped(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Mapped(int initialCapacity) {
        super(initialCapacity);
    }

    public Mapped() {
        super();
    }

    public Mapped(Map<? extends String, ?> m) {
        super(m);
    }

    @SuppressWarnings("unchecked")
    private final Children typedThis = (Children) this;

    public <T> T getByKey(String key) {
        return getByKey(key, null);
    }

    public <T> T getByKey(String key, T defaultValue) {
        return getByKey(key, defaultValue, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getByKey(String key, T defaultValue, Function<Object, T> cast) {
        Assert.notEmpty(key, "key cannot be empty");
        Object obj = this.get(key);
        if (obj == null) {
            return defaultValue;
        }
        if (cast == null) {
            return (T) obj;
        }
        return cast.apply(obj);
    }

    @Override
    public Children put(String name, Object value) {
        Assert.notEmpty(name, "key cannot be empty");
        super.put(name, value);
        return typedThis;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("Deprecated: method putAll(Map) in Mapped is forbidden. Use putMap(Map) instead.");
    }

    public Children putMap(Map<? extends String, ?> m) {
        Assert.notNull(m, "map cannot be null");
        super.putAll(m);
        return typedThis;
    }
}
