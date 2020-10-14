///*
// *
// *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.common.doc.properties;
//
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.License;
//import io.swagger.v3.oas.models.security.OAuthFlows;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.springdoc.core.SpringDocConfigProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author NorthLan
// * @date 2020-09-14
// * @url https://noahlan.com
// */
//@Data
//@Accessors(chain = true)
//@ConfigurationProperties(SpringdocProperties.PREFIX)
//public class SpringdocProperties {
//    public static final String PREFIX = "iti.springdoc";
//
//    /**
//     * 标题
//     **/
//    private String title = "";
//
//    /**
//     * 描述
//     **/
//    private String description = "";
//
//    /**
//     * 服务条款
//     **/
//    private String termsOfService = "";
//
//    /**
//     * 联系人信息
//     */
//    private Contact contact = new Contact();
//
//    /**
//     * 许可信息
//     */
//    private License license = new License();
//
//    /**
//     * 版本
//     **/
//    private String version = "";
//
//    /**
//     * 额外信息
//     */
//    private Map<String, Object> extensions = new HashMap<>();
//
//    /**
//     * swagger-ui 鉴权流程
//     */
//    private OAuthFlows oAuthFlows;
//
//    /**
//     * 默认组配置
//     */
//    private SpringDocConfigProperties.GroupConfig defaultGroup = DEFAULT_GROUP;
//
//    private final static SpringDocConfigProperties.GroupConfig DEFAULT_GROUP;
//
//    static {
//        SpringDocConfigProperties.GroupConfig groupConfig = new SpringDocConfigProperties.GroupConfig();
//        groupConfig.setGroup("default");
//        groupConfig.setPathsToMatch(Collections.singletonList("/**"));
//        DEFAULT_GROUP = groupConfig;
//    }
//}
