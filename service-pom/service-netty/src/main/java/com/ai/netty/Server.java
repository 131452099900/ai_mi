package com.ai.netty;

import com.ai.netty.server.handler.MyWebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Server {
    public static void main(String[] args) throws InterruptedException {
        new Server().run();
    }
    @Autowired
    private MyWebSocketHandler myWebSocketHandler;
    public void run() throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();
        server
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                // childHandler是在connect后的，所以是针对work线程的，而handler则是针对handler线程的
                .childHandler(new MyWebSocketHandler())
                .bind(6666)
                .sync();
    }

}
