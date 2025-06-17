### 1、仓库简介

欢迎访问 Oinone Pamirs，参与 Oinone 开源项目，与我们一起完善企业级产品化引擎。

此仓库为 Oinone 后端工程。

### 2、Oinone 简介
Oinone 是数式旗下企业级产品化引擎，一个以低代码驱动标准化研发与敏捷交付的一体化平台，聚焦软件企业的产品化转型需求，通过“标准化研发+敏捷交付”一体化架构，解决行业长期存在的标品开发与定制交付割裂难题，让企业回归“以产品驱动增长”的本质逻辑。

O 代表开源，谐音 All in one 之意，象征：一站式、敏捷响应、持续创新。

Onione 致力于构建一个开放的低代码和无代码生态系统，帮助客户创造价值，同时为开发者提供强有力的支持。我们相信，通过“共建”这一理念，能够汇聚更多的智慧与资源，推动开发者的创新与发展，让每一个创意都能在这个生态中绽放光彩。

#### 使命：_打造数智化时代软件产品的技术底座，为行业带来改变、为伙伴提供支撑、为客户创造价值_

### 3、产品体系
全方位覆盖产品研发及实施所需。
![产品体系](http://oinone-jar.oss-cn-zhangjiakou.aliyuncs.com/welcome-document/Open%20Source/zh-cn/%E4%BA%A7%E5%93%81%E4%BD%93%E7%B3%BB1.png "产品体系")
![产品体系](http://oinone-jar.oss-cn-zhangjiakou.aliyuncs.com/welcome-document/Open%20Source/zh-cn/%E4%BA%A7%E5%93%81%E4%BD%93%E7%B3%BB2.png "产品体系")

### 4、整体介绍
Oinone 是一个企业级产品化引擎，专注于业务软件产品构建及项目交付实施一体化，为企业在项目型、行业产品和标品的三个发展阶段提供极具成本效益的解决方案，帮助企业高效应对核心技术挑战，打造自有得研发平台。以软件工程管理理念为原则设计的平台架构如下：
![整体介绍](http://oinone-jar.oss-cn-zhangjiakou.aliyuncs.com/welcome-document/Open%20Source/zh-cn/%E6%95%B4%E4%BD%93%E4%BB%8B%E7%BB%8D.png "整体介绍")
- 核心能力层：可在不变更您技术栈的前提下，融合您原有技术能力、Oinone技术底座能力以及其他开源能力，提供互联网架构下的软件快速开发标准。Oinone 技术底座能力如下：
    - 100% 元数据驱动，支撑业务灵活配置与高效开发；
    - 高性能互联网架构封装，轻松应对高性能场景，如分布式、高并发等；
    - 具备完善的软件研发过程所需的通用能力，如用户、权限、消息、工作流、资源、国际化等；
    - 高度产品化的异构系统/异构数据源集成能力；
    - 完备的应用/模块全生命周期管理能力，如应用的创建、升级、依赖、消亡等。
- 标准业务产品层：可基于软件工程结构及通用数据模型，构建行业标准产品。
- 客户定制层：在标准业务产品层之上，高效继承、扩展，满足个性化所需。
- 无代码：满足产品开发/项目应用实施过程中的快速创新。
    - 超出标准产品范围之外的需求，每一个需求可被继承和扩展；
    - 针对标准产品进行个性化开发；
    - 适用于客户持续的适应性需求调整。

### 5、了解更多
- 了解更多关于 Oinone 的信息，您可以访问【[官网](https://www.oinone.top) | [Gitee-中文](https://gitee.com/oinone) | [Github-EN](https://github.com/Oinone)】。
- 【[后端框架-中文](https://gitee.com/oinone/oinone-pamirs) | [前端框架-中文](https://gitee.com/oinone/oinone-kunlun) | [Back-end Framework](https://github.com/oinone/oinone-pamirs) | [Front-end Framework](https://github.com/oinone/oinone-kunlun)】

### 6、工程概览
- pamirs-k2                    ：后端元数据核心功能
- pamirs-framework             ：后端核心功能包
- pamirs-framework-commons     ：后端核心功能公共包
- pamirs-framework-adaptor     ：后端核心功能扩展包
- pamirs-spi                   ：后端SPI基础功能包
- pamirs-boot                  ：后端应用启动包
- pamirs-core                  ：后端基础功能包
- pamirs-middleware            ：后端中间件功能包

### 7、快速入门
#### [安装与升级](https://guide.oinone.top/zh-cn/InstallOrUpgrade/)
- [环境准备](https://guide.oinone.top/zh-cn/InstallOrUpgrade/Dev-ENV/)
- [社区版：源码安装](https://guide.oinone.top/zh-cn/InstallOrUpgrade/CommunityEdition/source-code-installation.html)
- [企业版：快速体验：docker-full方式安装](https://guide.oinone.top/zh-cn/InstallOrUpgrade/EnterpriseEdition/docker-full-installation.html)
- [企业版：docker-mini方式安装](https://guide.oinone.top/zh-cn/InstallOrUpgrade/EnterpriseEdition/docker-mini-installation.html)
- [企业版：运行程序包方式安装](https://guide.oinone.top/zh-cn/InstallOrUpgrade/EnterpriseEdition/docker-full-installation.html)
#### [教程](https://guide.oinone.top/zh-cn/DevManual/Tutorials/)
- [设置指南](https://guide.oinone.top/zh-cn/DevManual/Tutorials/setup-guide.html)
- [章节 1：整体介绍（Architecture Overview）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter1-architecture-overview.html)
- [章节 2：新建一个应用（A New Application）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter2-a-new-application.html)
- [章节 3：模型与基础字段（Models And Basic Fields）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter3-models-and-basic-fields.html)
- [章节 4：安全简介（A Brief Introduction To Security）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter4-a-brief-introduction-to-security.html)
- [章节 5：界面实操（Finally, Some UI To Play With）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter5-Finally_%20some-UI-to-play-with.html)
- [章节 6：基础视图（Basic Views）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter6-basic-views.html)
- [章节 7：模型间关系（Relations Between Models）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter7-relations-between-models.html)
- [章节 8：字段间联动（Field Interlinkage）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter8-field-interlinkage.html)
- [章节 9：准备学习行为（Ready For Some Action）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter9-ready-for-some-action.html)
- [章节 10：约束（Constraints）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter10-constraints.html)
- [章节 11：追加细节（Add The Sprinkles）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter11-add-the-sprinkles.html)
- [章节 12：继承（Inheritance）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter12-inheritance.html)
- [章节 13：模块间相互作用（Interact With Other Modules）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter13-interact-with-other-modules.html)
- [章节 14：产品的个性化开发（Customized Features）](https://guide.oinone.top/zh-cn/DevManual/Tutorials/Back-endFramework/chapter14-customized-features.html)

### 8、视频教程
- [[数式Oinone] #产品化演示# 后端研发与无代码辅助](https://www.bilibili.com/video/BV1eDMizYEts/?vd_source=ee004011d0afa992f50d15c8738450b3)
- [[数式Oinone] #个性化二开# 后端逻辑](https://www.bilibili.com/video/BV1RDMizYELg/?vd_source=ee004011d0afa992f50d15c8738450b3)
- [[数式Oinone] #产品化演示# 前端开发](https://www.bilibili.com/video/BV1DoMizbECt/?vd_source=ee004011d0afa992f50d15c8738450b3)
- [[数式Oinone] #个性化二开# 前端交互](https://www.bilibili.com/video/BV1eDMizYED1/?vd_source=ee004011d0afa992f50d15c8738450b3)
- [[数式Oinone] #个性化二开# 无代码模式](https://www.bilibili.com/video/BV1eDMizYEdF/?vd_source=ee004011d0afa992f50d15c8738450b3)

### 9、演示环境
- [直达演示环境](https://demo.oinone.top)
- 账号：admin
- 密码：admin

### 10. 文档链接
- [安装与升级](https://guide.oinone.top/zh-cn/InstallOrUpgrade)
- [用户手册](https://guide.oinone.top/zh-cn/UserHandbook)
- [研发手册](https://guide.oinone.top/zh-cn/Development)
- [产品白皮书](https://www.oinone.top/whitePaper)
- [最佳范式](https://guide.oinone.top/zh-cn/DevManual/R_DParadigm)
- [常见解决方案](https://guide.oinone.top/zh-cn/DevManual/CommonSolutions)
- [常见问题](https://guide.oinone.top/zh-cn/DevManual/FAQ)

### 11、开发注意事项
Idea 导入工程后确保激活 Profile：打开 `Maven` 工具栏并且激活 `tools` Profile。
![Tools Profile](http://oinone-jar.oss-cn-zhangjiakou.aliyuncs.com/welcome-document/Open%20Source/zh-cn/tools-profile.png)

### 12、许可协议
- Oinone Pamirs 遵循 AGPL-3.0 协议。

### 13、如何参与
- 参与社区：您可以前往[[Oinone 社区](https://doc.oinone.top/)]与大家互动哦❤️
- [参与贡献](https://guide.oinone.top/zh-cn/Contribute)

### 14、联系我们
- 官网：https://www.oinone.top
- 邮箱：oinone@shushi.pro
- 小助手：![assistant](http://oinone-jar.oss-cn-zhangjiakou.aliyuncs.com/welcome-document/Open%20Source/zh-cn/Oinone%E5%B0%8F%E5%8A%A9%E6%89%8B.png)