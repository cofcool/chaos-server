**0.3.0**:

* 添加`component-processor`模块, 在编译时处理使用`@BaseComponent`注解的"Service"之间的依赖关系
* 优化`security-*`模块
  * `User.mobile`类型改为"String"
  * `AuthUserService`重名为`UserAuthorizationService`, 添加`reportAuthenticationExceptionInfo`方法
* 执行状态改为`ResultState`封装

**0.3.1**:

* Shiro 配置优化, 应用未配置`ShiroFilterFactoryBean`时会创建默认实例
* 优化`data-*`模块
* 全局异常处理错误

**0.3.2**:

* Shiro 升级到`1.4.0`, 并移除"shiro-ehcache"(Ehcache 版本过低, 与 Spring 监控不兼容)
* 其它问题修复

**0.4.0-SNAPSHOT**:

* 简化`User`结构
* 修改`ExecuteResult.entity`实现, 如果执行失败, 调用时会抛出异常
* `ExecuteResult`添加`orElse`方法, 简化`result`方法
* 优化`AbstractApiInterceptor`, 移除`ExcludeType`
* 移除`security-shiro`模块对的`data-redis`依赖
* 优化验证码处理逻辑
* 优化配置, 按照`Spring Boot`自动配置的方式进行配置
* 优化对异常描述码和描述信息的处理, 可自定义异常描述信息, 详情参考`ExceptionCodeManager`
* `ValidateInterceptor`等配置改为可选项，默认不创建接口参数验证, 日志打印等拦截器
* `LoggingInterceptor`移除对`Jackson`的依赖, 改为调用对象的`toString`方法
* 接口类的`getXxx()`风格代码改为`xxx()`
* `UserAuthorizationService` 删除 `setUserProcessor` 方法
* 修改`UserAuthorizationService.checkPermission()`的参数
* `security-spring`模块基本完成