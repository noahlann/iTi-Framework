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
 * 领域模型，对应DDD的聚合根
 * <p>
 * <p>世界由客体组成，主体认识客体的过程也是主体改造客体的过程</p>
 * <p>{@code IDomain}是客体，{@code IDomainService}是主体</p>
 * <p>客体是拟物化，体现状态；主体是拟人化，体现过程</p>
 * <p>应用程序的本质是认识世界（读），和改造世界（写）的过程</p>
 * <p>不要强行充血，把主体改变客体的逻辑写到领域模型里：认清主体和客体的关系！</p>
 * <p>
 * <p>领域对象为限界上下文(BC)中受保护对象，绝对不应该将其暴露到外面！</p>
 *
 * @author NorthLan
 * @date 2021-01-26
 * @url https://noahlan.com
 */
public interface IDomain extends IEntity {
}
