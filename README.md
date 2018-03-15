## mmall-beta 学习日志

## **maven 环境隔离**
实际项目开发当中我们会用到不止一下三种环境，开发环境、测试环境、生产环境。使用maven将各个环境进行隔离，方便开发、同时也方便上线。让因为配置文件不一致、环境没有更换这些问题导致项目异常不存在。
+ `dev`(开发环境) 
+ `beta`(测试环境) 
+ `prod`(生产环境) 

## Redis学习

### redis安装（window/linux） 

关于redis的安装，可以参考前一篇文章[**如何安装redis和简单使用**](https://ccoder.cc/2018/01/18/first-redis/)

### redis数据结构/基本命令 

### 键命令

## PropertiesUtil工具类

### 对PropertiesUtil进行了优化性的封装，提供获取`Integer` `Boolean` `String`等不同类型配置文件value的方法

## redisPool连接池

### redisPool连接池封装

+ `redisPool`连接池的完善
+ 获取jedis对象`getRedis()`
+ 返回资源`returnResource()`
+ `returnBrokenResource()`使用

### 学习jedis源码

## lombok工具

### IDE工具安装lombok工具

### 引入其依赖,使用注解

### 使用lombok注解
+ `@Sl4j `
+ `@Setter` 
+ `@Getter` 
+ `@Data`
+ 区分`@Data`和 `@Setter``@Getter`使用有什么区别，各自含义


## RedisPoolUtil工具类

### RedisPoolUtil工具类封装

### 提供对Redis的set get del setEx expire等操作的方法

## JsonUtil工具类

### JsonUtil工具类封装

封装`JsonUtil`工具类，提供json和object之间相互转换方法，同时提供json转List<User> Map<User,Category>等复杂集合对象的转换方法
+ json和object之间相互转换方法

+ json转List<User> Map<User,Category>等复杂集合对象的转换方法

### JsonUtil中objectMapper当中各种属性的配置 

 + `objectMapper.setSerializationInclusion(Inclusion.ALWAYS); 对象的所有字段全部列入序列化`  
 + `objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false); 取消默认转换timestamps`
 + `objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false); 忽略空bean转json错误`
 + `objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT)); 所有的日期格式都统一为以下格式：yyyy-MM-dd HH:mm:ss`
 + `objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 反序列化时，忽略在json字符串当中存在，但是在java对象当中不存在的对应属性的情况，防止错误`
 
## Tomcat集群
在`IDEA`当中启动两个`Tomcat`，来模拟`Tomcat`集群。
### `An invalid domain [.dianpoint.com] was specified for this cookie` 这个异常，使用cookie时候，Tomcat8和Tomcat7不太一样。
 + 解决方法：在Tomcat8的conf/content.xml当中添加如下配置
   `<CookieProcessor className="org.apache.tomcat.util.http.LegacyCookieProcessor" />`
   
   [An invalid domain was specified for this cookie](https://stackoverflow.com/questions/42524002/an-invalid-domain-was-specified-for-this-cookie)
   
   [How to change Cookie Processor to LegacyCookieProcessor in tomcat 8](https://stackoverflow.com/questions/38696081/how-to-change-cookie-processor-to-legacycookieprocessor-in-tomcat-8)
  
## CookieUtil工具类封装
  ### 熟悉在客户端种cookie的domain跨域问题

  ### 封装`CookieUtil`
  封装常用对cookie操作的read write del方法
 + `String readLoginToken(HttpServletRequest request)` 读cookie操作
 + `void writeLoginToken(HttpServletResponse response, String token)` 写cookie操作
 + `void delLoginToken(HttpServletRequest request, HttpServletResponse response)` 删除cookie操作
  
   
## 单点登录Session共享

### 单点登录

 + **登录操作**

 将用户登录成功之后的sessionId存入cookie取名为loginToken，同时将其存入redis当中，key=sessionId value=User实体的Json字符串，同时设置redis当中数据过期时间(用户登录过期时间)

 + **查看用户信息**
 
 从cookie当中获取loginToken,如果loginToken为null则说明用户未登录，直接返回,然后从redis当中获取key=loginToken的value,并将其Json反序列化成User对象，然后返回

 + **登出操作**
 
 从cookie当中获取到loginToken，然后从redis当中key=loginToken的value
 
 + **存在问题**
 
 loginToken存储在cookie当中存在有效时间，现在不能满足，用户进行除登出操作之外的其他请求时自动刷新loginToken的过期时间，用户登录一定时间到期后(期间无论是否进行其他操作)
 都会让用户再次登录。而我们需要的是，在有效期之内只要用户发送了除登出操作之外的请求，都会自动更新这个过期时间
 
### 解决上述`单点登录`存在问题
 + **使用原生过滤器**
 
 `SessionExpireFilter`就是为了解决用发送请求时重置种下的loginToken 过期时间，这样就可以避免用户登录后30分钟有需要再次重新登录。
 现在只存在登录成功后30分钟之内不发送任何请求，redis当中的loginToken则会过期


## Redis分布式

### Redis分布式算法原理(一致性算法)

+ 传统分布式算法
+ Consistent hashing 一致性算法原理
+ Hash倾斜性
+ 虚拟节点
+ Consistent hashing命中率


### Redis分布式环境配置

### Redis分布式服务端及客户端启动

### 封装分布式Shared Redis API

### 集群和分布式概念区分


## Spring-session 学习

### 查看spring-session 官方文档

### spring-session-data-redis 学习

## 联系

[聪聪](https://ccoder.cc/)的独立博客 ，一个喜欢技术，喜欢钻研的95后。如果你看到这篇文章，千里之外，我在等你联系。

- [Blog@ccoder's blog](https://ccoder.cc/)
- [CSDN@ccoder](http://blog.csdn.net/chencong3139)
- [Github@ccoder](https://github.com/chencong-plan)
- [Email@ccoder](mailto:admin@ccoder.top) *or* [Gmail@ccoder](mailto:chencong3139@gmail.com)
