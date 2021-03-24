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

package org.lan.iti.codegen.mp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.codegen.config.MPGeneratorProperties;
import org.lan.iti.codegen.util.Resources;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
        // 设置 outputDir 支持直接填写模块名
        GlobalConfig globalConfig = properties.getGlobalConfig();
        globalConfig.setOutputDir(getOutputDir(globalConfig.getOutputDir(), globalConfig.isKotlin(), true));
        // 设置 pathInfo
        initModulePathInfo(properties);
        autoGenerator.setGlobalConfig(properties.getGlobalConfig());
        autoGenerator.setDataSource(properties.getDataSourceConfig());
        autoGenerator.setPackageInfo(properties.getPackageConfig());
        autoGenerator.setStrategy(properties.getStrategyConfig());
        autoGenerator.setTemplate(properties.getTemplateConfig());
        MPGeneratorProperties.TemplateEngineType engineType = properties.getTemplateEngineType();
        if (!isEngineAvailable(engineType)) {
            log.error("当前选择模板引擎: {} 不存在，请检查项目依赖配置是否正确引入相关模板引擎包.", engineType.name());
            throw new RuntimeException("当前选择模板引擎不存在, 请检查项目依赖配置是否正确引入相关模板引擎包");
        }
        autoGenerator.setTemplateEngine(buildEngine(engineType.getTemplateClass()));

    }

    private String getOutputDir(String outputDir, boolean isKotlin, boolean isSrc) {
        String currentDir = System.getProperty("user.dir");
        String newOutputDir = outputDir;
        if (StrUtil.isBlank(newOutputDir)) {
            newOutputDir = StrUtil.join(File.separator, currentDir, "src", "main");
        } else {
            newOutputDir = StrUtil.join(File.separator, currentDir, newOutputDir, "src", "main");
        }
        String last;
        if (isSrc) {
            last = isKotlin ? "kotlin" : "java";
        } else {
            last = "resources";
        }
        newOutputDir += File.separator + last;
        return newOutputDir;
    }

    /**
     * 设置 pathInfo
     * <pre>
     *     仅modulePath有值时生效
     *     pathInfo若有填写，则不覆盖具体值
     * </pre>
     */
    private void initModulePathInfo(MPGeneratorProperties properties) {
        PackageConfig pc = properties.getPackageConfig();
        GlobalConfig gc = properties.getGlobalConfig();
        TemplateConfig tc = properties.getTemplateConfig();
        MPGeneratorProperties.ModulePath modulePath = properties.getModulePath();

        boolean isKotlin = gc.isKotlin();
        Map<String, String> pathInfo = pc.getPathInfo();
        Map<String, String> packageInfo;

        if (StrUtil.isNotBlank(modulePath.getApiOutputDir()) &&
                StrUtil.isNotBlank(modulePath.getServerOutputDir())) {
            String apiOutputDir = getOutputDir(modulePath.getApiOutputDir(), isKotlin, true);
            String serverOutputDir = getOutputDir(modulePath.getServerOutputDir(), isKotlin, true);
            String xmlOutputDir = getOutputDir(modulePath.getServerOutputDir(), isKotlin, false);
            // 包信息 copy from ConfigBuilder#
            packageInfo = new HashMap<>(8);
            packageInfo.put(ConstVal.MODULE_NAME, pc.getModuleName());
            // api
            packageInfo.put(ConstVal.ENTITY, joinPackage(pc.getParent(), pc.getEntity()));
            // server
            packageInfo.put(ConstVal.MAPPER, joinPackage(pc.getParent(), pc.getMapper()));
            packageInfo.put(ConstVal.SERVICE, joinPackage(pc.getParent(), pc.getService()));
            packageInfo.put(ConstVal.SERVICE_IMPL, joinPackage(pc.getParent(), pc.getServiceImpl()));
            packageInfo.put(ConstVal.CONTROLLER, joinPackage(pc.getParent(), pc.getController()));
            packageInfo.put(ConstVal.XML, joinPackage(null, pc.getXml()));

            // set pathInfo
            if (pathInfo == null) {
                pathInfo = new HashMap<>(6);
            }
            setPathInfo(pathInfo, packageInfo, tc.getEntity(isKotlin), apiOutputDir, ConstVal.ENTITY_PATH, ConstVal.ENTITY);
            setPathInfo(pathInfo, packageInfo, tc.getXml(), xmlOutputDir, ConstVal.XML_PATH, ConstVal.XML);
            setPathInfo(pathInfo, packageInfo, tc.getMapper(), serverOutputDir, ConstVal.MAPPER_PATH, ConstVal.MAPPER);
            setPathInfo(pathInfo, packageInfo, tc.getService(), serverOutputDir, ConstVal.SERVICE_PATH, ConstVal.SERVICE);
            setPathInfo(pathInfo, packageInfo, tc.getServiceImpl(), serverOutputDir, ConstVal.SERVICE_IMPL_PATH, ConstVal.SERVICE_IMPL);
            setPathInfo(pathInfo, packageInfo, tc.getController(), serverOutputDir, ConstVal.CONTROLLER_PATH, ConstVal.CONTROLLER);
            pc.setPathInfo(pathInfo);
        }
    }

    private void setPathInfo(Map<String, String> pathInfo, Map<String, String> packageInfo, String template, String outputDir, String path, String module) {
        if (!pathInfo.containsKey(path) && StrUtil.isNotBlank(template)) {
            pathInfo.put(path, joinPath(outputDir, packageInfo.get(module)));
        }
    }

    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    private String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

    /**
     * 连接父子包名
     *
     * @param parent     父包名
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    private String joinPackage(String parent, String subPackage) {
        if (StringUtils.isBlank(parent)) {
            return subPackage;
        }
        return parent + StringPool.DOT + subPackage;
    }
}
