<p align="center">
 <img src="https://img.shields.io/badge/iTi--Framework-2021.1.0--SNAPSHOT-green.svg" alt="Build Status">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2020-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-2.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/github/license/crazy6995/iTi-Framework" alt="Licence" />
</p>

# 简要说明

> 本项目用于快速搭建微服务，仅需简单引入相关包即可完成搭建。

- 基于 Spring Cloud 2020、Spring Boot 2.5、Spring Cloud Alibaba 2021.x、Sentinel、Nacos 2.x 的微服务快速开发框架
- 支持 Mybatis、Mybatis plus、Hibernate（主要）、Spring Data Jpa 等流行ORM框架
- 类 Spring Projects 的项目模块管理方式
- 自研授权服务器（iHA），支持Simple、OAuth2.0、OpenID Connect 1.0、自定义社交等多种身份认证方式；支持跨域单点登录（依赖于IAM项目，此项目后续开源）；支持多因素认证等特性...
- 领域驱动设计实践者，基于Axon-Framework
- 特有的：返回值动态过滤、资源自动扫描上报、统一化包装返回值、插件系统、多租户、基于DDD的代码生成（已废弃）、个性化的Extension（SPI）机制等诸多功能。


# 快速开始

## 核心依赖

| 依赖                   | 版本           |
| ---------------------- | ------------- |
| Spring Boot            | 2.5.5         |
| Spring Cloud           | 2020.0.4      |
| Spring Cloud Alibaba   | 2021.1        |
| Spring Data Jpa | 跟随Spring Boot |
| iTi iHA | 2021.1.0-SNAPSHOT         |
| Mybatis Plus           | 可选         |
| hutool                 | 5.7.3        |

## 模块说明

```lua
.
├── iti-cloud-project # Cloud项目，依赖SpringCloud相关包
│   ├── iti-cloud # cloud主体模块
│   ├── iti-cloud-autoconfigure # 自动装配模块
│   ├── iti-cloud-axon # axon依赖与基础模型模块
│   ├── iti-cloud-codegen # 代码生成模块
│   ├── iti-cloud-dependencies # iti-cloud-project 项目公共依赖管理
│   ├── iti-cloud-parent # iti-cloud-project 内所有项目的父级项目
│   ├── iti-cloud-security # 安全模块
│   ├── iti-cloud-sentinel # Sentinel支持模块
│   ├── iti-cloud-starters # 各类starters
│   │   ├── iti-cloud-starter
│   │   ├── iti-cloud-starter-axon
│   │   ├── iti-cloud-starter-data-jpa
│   │   ├── iti-cloud-starter-data-redis
│   │   ├── iti-cloud-starter-extension
│   │   ├── iti-cloud-starter-loadbalancer
│   │   ├── iti-cloud-starter-openfeign
│   │   ├── iti-cloud-starter-oss
│   │   ├── iti-cloud-starter-security
│   │   ├── iti-cloud-starter-sentinel
│   │   └── iti-cloud-starter-swagger
│   ├── iti-cloud-strategy # 策略模块，提供方便的策略模式使用
│   └── iti-cloud-test # 单元测试专有模块
├── iti-common-project # 底层依赖，iti-cloud-project依赖此模块
│   ├── iti-codegen # 代码生成器
│   ├── iti-common-core # 核心依赖模块
│   ├── iti-common-dependencies # iti-common-project 公共依赖管理
│   ├── iti-common-extension # 扩展Java-SPI机制，添加诸多特性
│   ├── iti-common-parent # iti-common-project 内所有项目的父级项目
│   └── iti-common-pay # 聚合支付底层依赖
├── iti-dependencies # iTi 框架的所有公共依赖管理
├── iti-iha-project # iHA 授权相关项目
│   ├── iti-iha-dependencies # iti-iha-project 公共依赖管理模块
│   ├── iti-iha-mfa # 多因素认证模块
│   ├── iti-iha-oauth2 # oauth2协议模块
│   ├── iti-iha-oidc # oidc协议模块，依赖oauth2
│   ├── iti-iha-parent # iti-iha-project 内所有项目的父级项目
│   ├── iti-iha-security # 安全模块，作为iha底包使用，提供通用功能
│   ├── iti-iha-server # 授权服务器模块
│   ├── iti-iha-simple # 简单登录模块
│   ├── iti-iha-social # 社交登录模块
│   └── iti-iha-sso # 单点登录模块，暂时使用baomidou的kisso
└── iti-sdk-project # SDK项目
    ├── iti-sdk-dependencies # iti-sdk-project 项目公共依赖管理模块 
    ├── iti-sdk-parent # iti-sdk-project 内所有项目父级项目
    └── iti-sdk-pay # 聚合支付SDK（java）
```

# 详细文档

> 攥写中，敬请期待……

# 开源协议

iTi 采用 [Apache 2.0 协议](https://www.apache.org/licenses/LICENSE-2.0.html)

允许商业使用，但务必保留代码作者、版权信息。

# 其它

暂无