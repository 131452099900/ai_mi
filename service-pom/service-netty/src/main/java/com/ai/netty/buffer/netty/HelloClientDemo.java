package com.ai.netty.buffer.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class HelloClientDemo {
    public static void main(String[] args) {
        ChannelFuture cf = new Bootstrap()
                .group(new NioEventLoopGroup())
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8889);

        cf.addListener((ChannelFutureListener) cfl -> {
            // 这里可以用cf，也可以用cfl，返回的都是同一个channel通道
            cf.channel().writeAndFlush("...");
        });


    }

}
