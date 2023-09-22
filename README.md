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

## 构建工具

- ![img](https://camo.githubusercontent.com/ed0b45f0a053bd31b8c5fec7561487b69a611726eefceafa758601ca6b76a63b/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4170616368655f4b61666b612d3233314632303f7374796c653d666f722d7468652d6261646765266c6f676f3d6170616368652d6b61666b61266c6f676f436f6c6f723d7768697465) 
- ![mysql](https://camo.githubusercontent.com/a4a4a017a5d519d7c4ce2a3cd3d2194fb7af4b1ca424850784565007c2acc7d8/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4d7953514c2d3030354338343f7374796c653d666f722d7468652d6261646765266c6f676f3d6d7973716c266c6f676f436f6c6f723d7768697465) 

- ![img](https://camo.githubusercontent.com/16c5d674d150e47e77738a333e74716023295715c956aaf84615cef3f50675ed/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f72656469732d2532334444303033312e7376673f267374796c653d666f722d7468652d6261646765266c6f676f3d7265646973266c6f676f436f6c6f723d7768697465) 
- ![img](https://camo.githubusercontent.com/dbee61c2c12189e4c8367bc24f9ab5a2fd0ccb730525950479bb4b27ee35cbe2/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f456c61737469635f5365617263682d3030353537313f7374796c653d666f722d7468652d6261646765266c6f676f3d656c6173746963736561726368266c6f676f436f6c6f723d7768697465) 

- ![img](https://camo.githubusercontent.com/d63d473e728e20a286d22bb2226a7bf45a2b9ac6c72c59c0e61e9730bfe4168c/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f48544d4c352d4533344632363f7374796c653d666f722d7468652d6261646765266c6f676f3d68746d6c35266c6f676f436f6c6f723d7768697465) 

## Quick start

1. **克隆仓库**

   使用以下命令克隆这个仓库：

```bash
 git clone https://github.com/yourusername/campus-forum.git
 cd campus-forum
```



[![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=xiaoxinxing66)](https://github.com/xiaoxinxing66/Campus_communication_platform)

如果您对项目有任何疑问或建议，请随时联系我们。感谢您的支持和参与！
