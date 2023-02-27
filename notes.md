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