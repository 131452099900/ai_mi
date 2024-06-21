package com.ai.netty.buffer.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;

import java.util.concurrent.TimeUnit;

public class EventLoopGroupDemo {
    public static void main(String[] args) {
        EventLoopGroup threadPool = new NioEventLoopGroup(2);
        threadPool.execute(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        threadPool.next().execute(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        threadPool.next().schedule(() -> {
            System.out.println(Thread.currentThread().getName());
        }, 3, TimeUnit.SECONDS);

        threadPool.schedule(() -> {
            System.out.println(Thread.currentThread().getName());
        }, 3, TimeUnit.SECONDS);
        // 2个核心线程数，那么只有2个EventLoop，因为一个EventLoop对应一个Thread
        System.out.println(threadPool.next());
        System.out.println(threadPool.next());
        System.out.println(threadPool.next());
    }
}
