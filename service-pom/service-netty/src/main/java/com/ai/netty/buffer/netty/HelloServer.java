package com.ai.netty.buffer.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        serverBootstrap
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2)) // 事件线程池组 boss worker
                .channel(NioServerSocketChannel.class)  // 通道
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        // 如果收到的是心跳包，则给客户端做出一个回复
                        if ("I am Alive".equals(msg)){
                            ctx.channel().writeAndFlush("I know");
                        }
                        System.out.println("收到客户端消息：" + msg);
                        super.channelRead(ctx, msg);
                    }
                    @Override
                    protected void initChannel(NioSocketChannel nch) throws Exception {
                        nch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                        nch.pipeline().addLast(new LoggingHandler());
                        nch.pipeline().addLast("IdleStateHandler",new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                        nch.pipeline().addLast(new HeartBeatServerHandler());

                    }
                });
        serverBootstrap
                .bind(8889);

    }
}
