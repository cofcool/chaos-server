# 项目说明

基于 Spring Boot 的 Java Web Server 框架, 简化开发, 封装了常见的企业级项目开发框架, 如 `Mybaits`, `Spring Security` 等。


通过`maven`引入:

```
<dependency>
  <groupId>net.cofcool.chaos</groupId>
  <artifactId>chaos-server-${MODULE_NAME}</artifactId>
  <version>0.3.2</version>
</dependency>
```

### 模块说明

第三方依赖:

1. Spring Boot
2. Spring data Jpa
3. MyBatis
4. Mybatis-PageHelper
5. Jackson
7. Shiro
8. Spring Security
9. Spring data Redis
10. Ehcache
10. ...


项目模块:

1. common
2. core
3. data-jpa
4. data-mybatis
5. data-redis
6. security-shiro
7. security-spring
8. actuator
9. component-processor
10. boot-starter

### 配置 


```properties
# 项目运行模式
chaos.development.mode=dev
# 项目版本
chaos.development.version=100
# 是否开启验证码
chaos.auth.using-captcha=false
# 定义扫描 Scanned 注解的路径
chaos.development.annotation-path=net.cofcool.chaos.server.demo
# shiro授权路径配置
chaos.auth.urls=/auth/**\=anon\n/error\=anon\n/**\=authc
# 登陆路径
chaos.auth.login-url=/auth/login
# 注入数据key配置, 多个时以","分隔
chaos.auth.checked-keys=id
```

### 使用

#### 授权处理

封装了`Apache Shiro`和`Spring Security`, 应用可依赖`security-shiro`模块或`security-spring`模块来实现授权管理。相关使用可参考`AuthService`, `UserAuthorizationService`。

#### 业务处理

封装了`Mybatis`和`Spring JPA`, 应用可依赖`data-mybatis`模块或`data-jpa`模块来实现授权管理。

业务相关Service可继承`DataAccess`接口, 实现类可继承`SimpleService`抽象类。

使用`Page`类封装分页的相关数据, ORM模块的`Paging`继承并扩展。

* Mybatis: 通过`PageHelper`分页。
* Jpa: 通过`Pageable`分页。

异常处理时, 自定义业务相关异常可继承`ServiceException`。如需设定异常级别, 实现`ExceptionLevel`接口即可, 该级别影响异常的打印。`ServiceException`已实现该接口, 默认为最高级别。

更多文档补充中......