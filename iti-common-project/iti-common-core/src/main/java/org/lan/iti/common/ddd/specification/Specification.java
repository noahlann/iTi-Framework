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

package org.lan.iti.common.ddd.specification;

/**
 * @author NorthLan
 * @date 2021-04-16
 * @url https://noahlan.com
 */
public class Specification<T> {
    /**
     * 创建空规约器
     *
     * @param <T> 实体类型
     * @return 代理规约器
     */
    public AbstractSpecification<T> create() {
        return new DelegateSpecification<>();
    }

    /**
     * 创建默认规约器
     *
     * @param specification 规约器
     * @param <T>           实体类型
     * @return 代理规约器
     */
    public static <T> AbstractSpecification<T> create(AbstractSpecification<T> specification) {
        return new DelegateSpecification<>(specification);
    }
}
