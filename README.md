# Campus_communication_platform
![109313046_0_final](https://github.com/xiaoxinxing66/Campus_communication_platform/assets/93857716/e4d31f0a-d9f6-44c5-9d08-75cc67fced7a)

欢迎来到校园交流论坛项目！这个论坛是一个旨在促进校园社交和信息分享的在线平台。无论你是学生、教职工，还是校友，都可以在这里参与讨论、分享知识和建立联系。

## Major function

底层基于`Spring Boot` 。项目可以分为几个模块：
1. 权限模块【负责用户注册、登录、权限控制】，主要用到的技术是Spring Security、Interceptor拦截器。
2. 核心模块【首页、帖子、评论、私信、统一异常处理、统一记录日志】，异常处理和记录日志主要运用了Spring AOP的思想，针对项目中的代码进行一个横向的扩展。
3. 性能模块【点赞、关注、用户缓存、统计数据】，主要使用Redis保存点赞和关注、统计UV、DAU数据。并且缓存用户的信息、验证码、登录凭证到Redis，减小数据库访问压力。
4. 通知模块【系统通知，点赞、评论、关注】，主要使用Kafka消息队列来发送通知，并且将通知转换为Message存到表中，使用id判重来解决kafka重复发送数据的问题。
项目介绍：项目基于Spring Boot，大体可以分为四个模块，权限、核心、性能、通知。
权限模块比如用户的登录注册，权限控制；核心模块比如首页，帖子，统一异常处理，统一记录日志；性能模块基于Redis包括点赞，关注，统计数据；通知模块基于Kafka，使用Kafka来发送通知。

## 



<li><a href="https://getbootstrap.com" rel="nofollow"><img src="https://camo.githubusercontent.com/b13ed67c809178963ce9d538175b02649800772be1ce0cb02da5879e5614e236/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f426f6f7473747261702d3536334437433f7374796c653d666f722d7468652d6261646765266c6f676f3d626f6f747374726170266c6f676f436f6c6f723d7768697465" alt="Bootstrap" data-canonical-src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&amp;logo=bootstrap&amp;logoColor=white" style="max-width: 100%;"></a></li>


<li><a href="https://jquery.com" rel="nofollow"><img src="https://camo.githubusercontent.com/15b7da9c5e50455ef7c50a5d642afad7ab8d752e575010116727c3865beb026d/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6a51756572792d3037363941443f7374796c653d666f722d7468652d6261646765266c6f676f3d6a7175657279266c6f676f436f6c6f723d7768697465" alt="JQuery" data-canonical-src="https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&amp;logo=jquery&amp;logoColor=white" style="max-width: 100%;"></a></li>

## Quick start

1. **克隆仓库**

   使用以下命令克隆这个仓库：

```bash
 git clone https://github.com/yourusername/campus-forum.git
 cd campus-forum
```



[![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=xiaoxinxing66)](https://github.com/xiaoxinxing66/Campus_communication_platform)

如果您对项目有任何疑问或建议，请随时联系我们。感谢您的支持和参与！
