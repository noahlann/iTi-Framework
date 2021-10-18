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

package org.lan.iti.cloud.security.access;

import java.io.Serializable;

/**
 * 存储与安全系统相关的配置属性
 * <p>
 * 在运行时与其他 <code>ConfigAttribute<code> 一起存储，用于相同的安全对象目标。
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public interface ConfigAttribute extends Serializable {
    /**
     * If the <code>ConfigAttribute</code> can be represented as a <code>String</code> and
     * that <code>String</code> is sufficient in precision to be relied upon as a
     * configuration parameter by a {@link RunAsManager}, {@link AccessDecisionManager} or
     * <code>AccessDecisionManager</code> delegate, this method should return such a
     * <code>String</code>.
     * <p>
     * If the <code>ConfigAttribute</code> cannot be expressed with sufficient precision
     * as a <code>String</code>, <code>null</code> should be returned. Returning
     * <code>null</code> will require any relying classes to specifically support the
     * <code>ConfigAttribute</code> implementation, so returning <code>null</code> should
     * be avoided unless actually required.
     *
     * @return a representation of the configuration attribute (or <code>null</code> if
     * the configuration attribute cannot be expressed as a <code>String</code> with
     * sufficient precision).
     */
    String getAttribute();
}
