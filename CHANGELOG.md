**0.3.0**:

TODO:

* 添加`component-processor`模块，在编译时处理使用`@BaseComponent`注解的"Service"之间的依赖关系
* 优化`security-*`模块
  * `User.mobile`类型改为"String"
  * `AuthUserService`重名为`UserAuthorizationService`, 添加`reportAuthenticationExceptionInfo`方法