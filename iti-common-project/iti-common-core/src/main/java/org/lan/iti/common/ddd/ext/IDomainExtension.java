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

package org.lan.iti.common.ddd.ext;

/**
 * 扩展点：业务语义确定，但执行逻辑不同的业务功能点，即以不变应万变.
 * <p>
 * <p>通过扩展点这个接口，实现业务的多态.</p>
 * <p>扩展点是分层的，在此基础上实现了：领域步骤的多态，领域模型的多态</p>
 * <p>ATTENTION: 扩展点方法的返回值，必须是Java类，而不能是int/boolean等primitive types，否则可能抛出NPE!</p>
 *
 * @author NorthLan
 * @date 2021-02-08
 * @url https://noahlan.com
 */
public interface IDomainExtension extends IPlugable {
    String DEFAULT_CODE = "_default_";
}
