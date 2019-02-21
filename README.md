# 项目说明

基于 Spring Boot 的 Java Web Server 框架，简化开发 。


通过`maven`引入:

```
<dependency>
  <groupId>net.cofcool.chaos</groupId>
  <artifactId>chaos-server-${MODULE_NAME}</artifactId>
  <version>0.2.0</version>
</dependency>
```

### 命令示例

运行模式：

* dev
* test
* release

打包:

```sh
# release打包
mvn clean -Prelease install
```

运行：
```sh
java -jar chaos-server.jar > chaos-server.log 2>&1 &
```

关闭应用程序(grep查询的关键词视运行命令而定)：

```sh
#!/bin/bash
pid=`jps -l | grep chaos-server.jar | awk '{ print $1 }'`
echo $pid
if [ -n "$pid" ];then
        kill -9 $pid
fi
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


目前有8个模块

1. common
2. core
3. data-jpa
4. data-mybatis
5. data-redis
6. security-shiro
7. security-spring
8. actuator
9. component-processor

### 配置 

配置文件:
 
*.properties

必需组件:

* UserAuthorizationService (chaos-server-security-*): 授权相关
* JpaConfig (chaos-server-data-jpa): JPA配置，DataSource等
* PasswordProcessor (chaos-server-security-*): 密码处理，加密解密

### 使用

业务相关Service可继承`DataAccess`接口，实现类可继承`SimpleService`抽象类。


使用`Page`类封装分页的相关数据，ORM模块的`Paging`继承并扩展。

* Mybatis: 通过`PageHelper`分页。
* Jpa: 通过`Pageable`分页。

**异常**处理时，自定义业务相关异常需继承`ServiceException`。如需设定异常级别，实现`ExceptionLevel`接口即可，该级别影响异常的打印。`ServiceException`已实现该接口，默认为最高级别。

