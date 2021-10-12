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

package org.lan.iti.cloud.autoconfigure.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.lan.iti.cloud.jackson.module.ITILongModule;
import org.lan.iti.common.core.util.ReflectUtil;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * JacksonConfig
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class ITIJacksonAutoConfiguration {

    @SuppressWarnings("unchecked")
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);

            // 由于builder.modules会覆盖原设置,通过反射获取builder原先modules值
            Object originalModulesObj = ReflectUtil.getFieldValue(builder, "modules");
            List<Module> modules = new ArrayList<>();
            if (originalModulesObj instanceof List) {
                modules.addAll((List<Module>) originalModulesObj);
            }
//            modules.add(new ITIJavaTimeModule());
            modules.add(new ITILongModule());
            modules.add(new ParameterNamesModule());
            modules.add(new Jdk8Module());
            modules.add(new JavaTimeModule());
            builder.modules(modules);
        };
    }
}
