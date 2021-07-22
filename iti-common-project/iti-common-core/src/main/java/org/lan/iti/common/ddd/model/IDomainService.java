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

package org.lan.iti.common.ddd.model;

/**
 * 顶层领域服务接口
 * <p>
 * <p>领域服务是主体，主体认识和改造客体({@code IAggregateRoot})</p>
 * <p>本框架内，领域服务根据粒度的粗细分为3层：</p>
 * <pre>
 *               +--------------------+
 *               |  BaseDomainAbility |
 *      +-----------------------------|
 *      |                 IDomainStep |
 * +----------------------------------|
 * |                   IDomainService |
 * +----------------------------------+
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Deprecated
public interface IDomainService {
}
