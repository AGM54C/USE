package com.example1.demo2.util;

//全局线程工具
public class ThreadLocalUtil {
    //提供ThreadLocal对象
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键值获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    //释放ThreadLocal，防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
