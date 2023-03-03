#### 1.Java序列化JSON时long型数值,会出现精度丢失的问题。
  原因： java中得long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值).
  + 解决方式：我们可以在服务端给页面响应json数据时进行处理，将long型数据统一转为String字符串
  + 1)提供对象转换器JacksonObjectMapper，基于Jackson进行Java对象到json数据的转换（资料中已经提供，直接复制到项目中使用)
    2)在WebMvcConfig配置类中扩展Spring mvc的消息转换器，在此消息转换器中使用提供的对象转换器进行Java对象到json数据的转换
#### 2.
   + 客户端发送的每次http请求，对应的在服务端都会分配一个新的线程来处理，在处理过程中涉及到下面类中的方法都属于相同的一个线程:
     1、LoginCheckFilter的doFilter方法
     2、EmployeeController的update方法
     3、MyMetaObjectHandler的updateFill方法
     可以在上面的三个方法中分别加入下面代码（获取当前线程id):long id = Thread. currentThread().getIdo ;
     log.info("线程id: {0" ,id);
#### 3.ThreadLocal
     什么是ThreadLocal?
     + ThreadLocal并不是一个Thread，而是Thread的局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问。
     ThreadLocal常用方法:public void set(T value)设置当前线程的线程局部变量的值public T get()返回当前线程所对应的线程局部变量的值
     我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，并调用ThreadLocal的set方法来设置当前线程的线程局部变量的值(用户id)，
     然后在MyMetaobjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前线程所对应的线程局部变量的值 (用户id)。
#### 4.文件上传
     文件上传介绍
     文件上传，也称为upload，是指将本地图片、视频、音频等文件上传到服务器上，可以供其他用户浏览或下载的过程。
     文件上传在项目中应用非常广泛，我们经常发微博、发微信朋友圈都用到了文件上传功能。
     文件上传时，对页面的form表单有如下要求:
     method="post"
     采用post方式提交数据
     enctype="multipart/form-datal采用multipart格式上传文件
     type="file"
     使用input的file控件上传
#### 5.Spring Cache
     Spring Cache 介绍
     + Spring Cache是一个框架，实现了基于注解的缓存功能，只需要简单地加一个注解，就能实现缓存功能Spring Cache提供了一层抽象，底层可以切换不同的cache实现。具体就是通过CacheManager接口来统一不同的缓存技术。
     CacheManager是Spring提供的各种缓存技术抽象接口。
     EhCacheCacheManager 使用EhCache作为缓存技术
     GuavaCacheManager 使用Google的GuavaCache作为缓存技术
     RedisCacheManager 使用Redis作为缓存技术
     + 注解             说明
     @EnableCaching    开启缓存注解功能
     @Cacheable        在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据若没有数据，调用方法并将方法返回值放到缓存中
     @CachePut         将方法的返回值放到缓存中
     @CacheEvict       将一条或多条数据从缓存中删除
#### 6.SpEL
     Spring 表达式语言（简称“SpEL”）是一种强大的表达式语言，支持在运行时查询和操作对象图。语言语法类似于 Unified EL，但提供了额外的功能，最值得注意的是方法调用和基本的字符串模板功能。
     虽然 SpEL 是 Spring 产品组合中表达式评估的基础，但它不直接与 Spring 绑定，可以独立使用。
#### 7.读写分离
     + mysql主从复制
     MvSOL主从复制是一个异步的复制过程，底层是基于Mvsgl数据库自带的二进制日志功能。
     就是一台或多台MVSOL数库(slave，即从库)从另一台MSL数据库(master，即主库)进行日志的复制然后再解析志并应用到自身，
     最终实现从库的数据和主库的数据保持一致。MVSOL主从复制是MVSOL数据库自带功能，无需借助第三方工具
     + MySQL复制过程分成三步
     master将改变记录到二进制日志 (binary log)
     slave将master的binary og拷贝到它的中继日志 (relay log)
     slave重做中继日志中的事件，将改变应用到自己的数据库中

     Master(insert update delete)
     Slave(select)
#### 8.Sharding-JDBC
     sharding-JDBC定位为轻量级lava框架，在ava的]DBC层提供的额外服务。它使用客户端直连数据库，以jar包形式提供服务，无需额外部署和依赖，可理解为增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。使用Sharding-JDBC可以在程序中轻松的实现数据库读写分离。
     适用于任何基于]DBC的ORM框架，如: JPA,Hibernate, Mybatis, SpringJDBCTemplate或直接使用DBC。支持任何第三方的数据库连接池，如: DBCP,C3PO,BoneCP,Druid,HikariCP等。支持任意实现IDBC规范的数据库。目前支持MySOL，Oracle，SOLServer，PostresQL以及任何遵循SQL92标准的数据库
       
     
     
     

