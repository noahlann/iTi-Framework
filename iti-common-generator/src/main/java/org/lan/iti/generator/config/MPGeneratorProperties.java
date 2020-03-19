/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.generator.config;


import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 生成器配置
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
@Data
public class MPGeneratorProperties {
    /**
     * 生成器全局配置
     */
    private GlobalConfig globalConfig = new GlobalConfig();

    /**
     * 数据源配置
     */
    private DataSourceConfig dataSourceConfig = new DataSourceConfig();

    /**
     * 包配置
     */
    private PackageConfig packageConfig = new PackageConfig();

    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig = new StrategyConfig();

    /**
     * 模板配置
     */
    private TemplateConfig templateConfig = new TemplateConfig();

    /**
     * 模板引擎类型
     */
    private TemplateEngineType templateEngineType = TemplateEngineType.FREEMARKER;

    /**
     * 模板引擎类型
     */
    @AllArgsConstructor
    @Getter
    public enum TemplateEngineType {
        FREEMARKER("freemarker.template.Template", FreemarkerTemplateEngine.class),
        BEETL("org.beetl.core.GroupTemplate", BeetlTemplateEngine.class),
        VELOCITY("org.apache.velocity.app.VelocityEngine", VelocityTemplateEngine.class);

        private String engineClass;
        private Class<?> templateClass;
    }
}
