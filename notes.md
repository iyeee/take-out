#### Java序列化JSON时long型数值,会出现精度丢失的问题。
  原因： java中得long能表示的范围比js中number大,也就意味着部分数值在js中存不下(变成不准确的值).
  + 解决方式：我们可以在服务端给页面响应json数据时进行处理，将long型数据统一转为String字符串
  + 1)提供对象转换器JacksonObjectMapper，基于Jackson进行Java对象到json数据的转换（资料中已经提供，直接复制到项目中使用)
    2)在WebMvcConfig配置类中扩展Spring mvc的消息转换器，在此消息转换器中使用提供的对象转换器进行Java对象到json数据的转换
#### 