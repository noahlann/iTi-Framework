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

package org.lan.iti.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.generator.config.MPGeneratorProperties;
import org.lan.iti.generator.util.Resources;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;

/**
 * mybatis-plus 代码生成器
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
@Slf4j
@NoArgsConstructor
public class MPGenerator {
    private MPGeneratorProperties properties;
    private AutoGenerator autoGenerator;

    /**
     * static method
     * 自动读取配置文件进行配置，而后执行代码生成
     */
    public static void run() {
        MPGenerator mpg = new MPGenerator();
        mpg.setProperties();
        mpg.execute();
    }

    /**
     * 设置 InjectionConfig
     */
    public MPGenerator setCfg(InjectionConfig config) {
        this.setProperties();
        this.autoGenerator.setCfg(config);
        return this;
    }

    /**
     * 设置名称转换
     */
    public MPGenerator setNameConverter(INameConvert nameConverter) {
        this.setProperties();
        this.autoGenerator.getStrategy().setNameConvert(nameConverter);
        return this;
    }

    /**
     * 执行代码生成
     */
    public void execute() {
        this.setProperties();
        this.autoGenerator.execute();
    }

    /**
     * 根据class构建模板引擎
     *
     * @param templateEngineClass 用于构建的模板引起class
     */
    private AbstractTemplateEngine buildEngine(Class<?> templateEngineClass) {
        try {
            return (AbstractTemplateEngine) templateEngineClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("无法创建对应的模板引擎: {}", templateEngineClass.getSimpleName());
            e.printStackTrace();
        }
        throw new RuntimeException("无法创建模板引擎");
    }

    /**
     * 判定包是否存在
     *
     * @param engineType 模板引擎类型
     */
    private boolean isEngineAvailable(MPGeneratorProperties.TemplateEngineType engineType) {
        boolean isEngineAvailable = false;
        try {
            isEngineAvailable = null != Class.forName(engineType.getEngineClass());
        } catch (Throwable e) {
            // just catch it
        }
        return isEngineAvailable;
    }

    private void setProperties() {
        if (this.properties != null || this.autoGenerator != null) {
            return;
        }
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        Yaml yaml = new Yaml(representer);
        yaml.setBeanAccess(BeanAccess.FIELD);

        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("generator.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            this.properties = yaml.loadAs(inputStream, MPGeneratorProperties.class);
        }
        if (this.properties == null) {
            throw new RuntimeException("无法读取配置文件 generator.yml ,请检查配置文件是否正确写入.");
        }

        autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(properties.getGlobalConfig());
        autoGenerator.setDataSource(properties.getDataSourceConfig());
        autoGenerator.setPackageInfo(properties.getPackageConfig());
        autoGenerator.setStrategy(properties.getStrategyConfig());
        autoGenerator.setTemplate(properties.getTemplateConfig());
        // TODO InjectionConfig
        MPGeneratorProperties.TemplateEngineType engineType = properties.getTemplateEngineType();
        if (!isEngineAvailable(engineType)) {
            log.error("当前选择模板引擎: {} 不存在，请检查项目依赖配置是否正确引入相关模板引擎包.", engineType.name());
            throw new RuntimeException("当前选择模板引擎不存在, 请检查项目依赖配置是否正确引入相关模板引擎包");
        }
        autoGenerator.setTemplateEngine(buildEngine(engineType.getTemplateClass()));
    }
}
