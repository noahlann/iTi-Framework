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

package org.lan.iti.cloud.iha.core.model;

import cn.hutool.core.lang.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mapped generic type
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class Mapped implements Serializable {
    private static final long serialVersionUID = 2870321854370298093L;

    private final Map<String, Object> map;

    public Mapped() {
        map = new HashMap<>();
    }

    public Mapped(Map<String, ?> map) {
        Assert.notNull(map, "map cannot be null");
        this.map = new HashMap<>(map);
    }

    @SuppressWarnings("unchecked")
    public <T> T map(String name) {
        Assert.notEmpty(name, "name cannot be empty");
        return (T) this.map.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Mapped> T map(String name, Object value) {
        Assert.notEmpty(name, "name cannot be empty");
        Assert.notNull(value, "value cannot be null");
        this.map.put(name, value);
        return (T) this;
    }

    public Map<String, Object> mapped() {
        return this.map;
    }

    @SuppressWarnings("unchecked")
    public <T extends Mapped> T map(Consumer<Map<String, Object>> mapConsumer) {
        mapConsumer.accept(this.map);
        return (T) this;
    }
}
