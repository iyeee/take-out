package com.modq.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> implements Serializable {
    //编码 1成功 0和其他数字为失败
    private Integer code;
    //错误信息
    private String msg;
    //数据
    private T data;
    //动态数据
    private Map<String, Object> map=new HashMap<String, Object>();
    public static <T> R<T> success(T object){
        R<T> r=new R<T>();
        r.data=object;
        r.code=1;
        return r;
    }
    public static <T> R<T> error(String msg){
        R<T> r=new R<>();
        r.msg=msg;
        r.code=0;
        return  r;
    }
    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
