/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.jpa.generator;

import lombok.NoArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.lan.iti.common.core.util.idgen.IdGenerator;

import java.io.Serializable;

/**
 * snowflake id生成策略
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@NoArgsConstructor
public class SnowflakeId implements IdentifierGenerator {
    public static final String GENERATOR_NAME = "snowflakeId";
    public static final String STRATEGY_NAME = "org.lan.iti.cloud.jpa.generator.SnowflakeId";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return IdGenerator.nextStr();
    }
}
