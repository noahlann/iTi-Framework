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

package org.lan.iti.common.ddd;

import org.lan.iti.common.core.IBaseTranslator;

/**
 * DDD 分层模型数据转换接口
 * <p>
 * <p>建议使用MapStruct作为底层转换插件</p>
 *
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public interface IBaseDDDTranslator<Do, Po, Creator> extends IBaseTranslator<Do, Po> {

    /**
     * 将Do转换为Po
     *
     * @param aDo 源数据类型
     * @return 目标类型
     */
    default Po toPo(Do aDo) {
        return translate(aDo);
    }

    /**
     * 将Po转换为 Creator
     * <p>
     * <p>为保护领域模型，不对领域模型进行直接的set，由契约对象Creator作为对外开放标准</p>
     *
     * @param po Po对象
     * @return Creator
     */
    Creator toCreator(Po po);
}
