# coder-talk
## 项目背景
一个技术人员记录markdown博客，讨论的论坛项目。 可用于学习Spring框架，以及一个java web项目的完整开发流程
## 部署网站
[heganghuan.cn](http://heganghuan.cn) 使用guest，guest登录体验完整功能！
## 技术栈
后端：SpringBoot + Mybatis + Mysql + Redis + kafka

前端：BootStrap模板 + thymeleaf 模板引擎

## 实现的功能
1. 用户的注册与登录（涉及到验证码，自动发送邮件，登录状态保存）
2. 头像的修改（文件上传）
3. markdown文章的发布
4. 评论，以及楼中楼评论
5. 帖子的点赞，浏览量（redis Set, String ）
6. 好友关注（redis zset）
7. 看朋友圈帖子
8. 系统通知与朋友私信（kafka）

待完成的工作：
* 利用AOP完成全局异常捕获和错误页面跳转
* 利用SpringSecurity完成页面权限管理

## 如何部署
1. git clone
2. 修改application.yml 配置文件（数据库连接，mail配置，kafka配置，上传路径等）
3. maven打包jar包
4. 服务器安装配置mysql , kafka, nginx, 运行jar包
