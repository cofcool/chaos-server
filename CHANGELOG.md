**0.3.0**:

* 添加`component-processor`模块，在编译时处理使用`@BaseComponent`注解的"Service"之间的依赖关系
* 优化`security-*`模块
  * `User.mobile`类型改为"String"
  * `AuthUserService`重名为`UserAuthorizationService`, 添加`reportAuthenticationExceptionInfo`方法
* 执行状态改为`ResultState`封装

**0.3.1**:

* Shiro 配置优化，应用未配置`ShiroFilterFactoryBean`时会创建默认实例
* 优化`data-*`模块