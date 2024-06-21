package com.ai.netty.buffer.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        final NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nch) throws Exception {
//                        nch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                        nch.pipeline()
                                // 3s未写触发读闲置
                                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx)
                                            throws Exception {
                                        // 建立连接成功之后，先向服务端发送一条数据
                                        log.error("=======+>channelActive");
                                        ctx.channel().writeAndFlush("我是不会发心跳包的客户端-B！");
                                    }
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx)
                                            throws Exception {
                                        System.out.println("因为没发送心跳包，俺被开除啦！");
                                        // 当通道被关闭时，停止前面启动的线程池
                                        worker.shutdownGracefully();
                                    }
                                });

                    }

                }).connect("localhost",8889)
                .sync();
    }
}
