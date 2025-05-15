package com.example1.demo2;


import org.junit.jupiter.api.Test;

public class ThreadLocalTest {
    @Test
    public void testThreadLocalSetAndGet() {
        //提供一个对象
        ThreadLocal tl = new ThreadLocal();

        //开启两个进程
        new Thread(() -> {
            tl.set("威龙");
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
        },"麦小鼠").start();

        new Thread(() -> {
            tl.set("雷斯");
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
        },"赛伊德").start();
    }
}
