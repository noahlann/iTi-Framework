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
iti
```

# 详细文档

> 敬请期待

# 开源协议

iTi 采用 [Apache 2.0 协议](https://www.apache.org/licenses/LICENSE-2.0.html)

允许商业使用，但务必保留代码作者、版权信息。

# 其它

暂无