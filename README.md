# README

**Chaos Server**([ˈkāˌäs] [ˈsərvər]), 基于 Spring Boot 的 Java Web Server 框架, 简化开发, 封装了常见的企业级项目开发框架, 如 `Mybaits`, `Spring Security`, `Shiro`, `JPA`等。


最新版本为 ![maven central](https://img.shields.io/maven-central/v/net.cofcool.chaos/chaos-server.svg), Spring Boot 版本为 `2.4.13`, 通过`maven`引入

```
<dependency>
  <groupId>net.cofcool.chaos</groupId>
  <artifactId>chaos-server-${MODULE_NAME}</artifactId>
  <version>${VERSION}</version>
</dependency>
```

查看[更新日志](./CHANGELOG.md)

使用可查看 [示例项目](https://github.com/cofcool/chaos-server-demo.git)


### 模块说明

1. common 该模块定义了基本接口和类等
2. core 核心实现, 包括拦截器, 国际化, JSON 解析等
3. data-jpa `JPA` 相关的基础 `Service` 和工具类等
4. data-mybatis `Mybatis` 相关的基础 `Service` 和工具类等
5. extension 封装 `Redis`, `MongoDB` 等相关操作
6. security-shiro 封装了 `Shiro`, 简化开发流程
7. security-spring 封装了 `Spring Security`, 简化开发流程
8. component-processor 编译时扫描 `BaseComponent` 注解, 该注解可标识基础组件, 避免组件调用混乱
9. boot-starter 根据 `Spring Boot` 规范进行自动化配置

### 配置 

```properties
# 是否调试模式
chaos.development.debug=false
# 项目版本
chaos.development.version=100
# 是否开启验证码
chaos.auth.using-captcha=false
# 定义扫描 Scanned 注解的路径
chaos.development.annotation-path=net.cofcool.chaos.server.demo
# 授权路径配置, 用";"分割, "Spring Security" 项目, 该配置为匿名访问路径; "Shiro" 为授权路径配置, 即"filterChainDefinitions"
chaos.auth.urls=/auth/**\=anon;/error\=anon;/**\=authc
# 登陆路径
chaos.auth.login-url=/auth/login
# 注入数据key配置, 多个时以","分隔
chaos.auth.checked-keys=id
```

### 使用

业务相关Service可继承`DataAccess`接口, 实现类可继承`SimpleService`抽象类。


使用`Page`类封装分页的相关数据, ORM模块的`Paging`继承并扩展。

* Mybatis: 通过 `Mybatis Plus` 分页插件分页。
* Jpa: 通过`Pageable`分页。

**异常**处理时, 自定义业务相关异常需继承`ServiceException`。如需设定异常级别, 实现`ExceptionLevel`接口即可, 该级别影响异常的打印。`ServiceException`已实现该接口, 默认为最高级别。

#### 授权处理

封装了`Apache Shiro`和`Spring Security`, 应用可依赖`security-shiro`模块或`security-spring`模块来实现授权管理。

<img src="./docs/auth_login.svg" alt="login"/>

`AuthService`类定义了登录等操作, 应用不需要实现该类, 只需引用该组件即可, 登录由"filter"完成，因此不需要主动调用该方法。

`UserAuthorizationService`定义应用操作, 应用需实现该类。

`PasswordProcessor`定义密码处理操作, 应用需实现该类。

`User`存储用户信息。

`spring-security` 配置时注意 `chaos.auth.cors-enabled` 和 `chaos.auth.csrf-enabled` 配置项。

#### Service 层

`DataAccess`定义"Service"常用的方法。

`Result`封装运行结果，提供两个子接口，`QueryResult`封装查询结果，其它情况由`ExecuteResult`处理。

抽象类`SimpleService`实现`DataAccess`类，实现`query`方法，简化分页操作，定义`queryWithPage`方法，用于分页查询。通过`ExceptionCodeManager`描述执行状态错误码等。JPA 应用的"Service"可继承`SimpleJpaService`。

#### Controller 层

默认提供三种"Controller(使用`Scanned`注解)"代理类:

* 注入用户数据到请求参数中, 即数据隔离，实现类为`ApiProcessingInterceptor`
* 请求日志打印，实现类为`LoggingInterceptor`
* 参数校验，实现类为`ValidateInterceptor`

##### 1. Json解析

`ResponseBodyMessageConverter`支持把`Result`, `Number`, `String`, `Result.ResultState`等对象转为`Message`对象，即可保证接口返回的数据统一为`Message`规定的格式(), 应用可调用`Message.of()`方法创建实例。


#### 异常处理

##### 默认处理异常类型

`GlobalHandlerExceptionResolver`处理在请求中发生的异常，包括如下异常:

* ServiceException
* NullPointerException, IndexOutOfBoundsException,  NoSuchElementException
* HttpMessageNotReadableException
* UnsupportedOperationException
* MethodArgumentNotValidException
* DataAccessException

除以上异常外，其它异常由 Spring 的`DefaultHandlerExceptionResolver`处理。

把异常信息包装为`Message`对象，保证发生异常时的响应格式符合接口规范。

注意：当运行在`Debug`模式时，会优先调用`DefaultHandlerExceptionResolver`处理异常。

##### 描述信息

应用中的描述信息统一由 `ExceptionCodeManager` 处理。`ExceptionCodeDescriptor` 封装了描述信息，默认提供了两种类型:

* SimpleExceptionCodeDescriptor
* ResourceExceptionCodeDescriptor

`SimpleExceptionCodeDescriptor`为默认配置，包含了常见的描述代码和描述信息，例如操作成功，操作失败等。

`ResourceExceptionCodeDescriptor` 从"messages"文件中读取配置的描述信息。
 