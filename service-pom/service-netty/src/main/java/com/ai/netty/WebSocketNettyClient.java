package com.ai.netty;

import cn.hutool.json.JSONUtil;
import com.ai.common.core.utils.JsonUtils;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.codc.MsgDecoder;
import com.ai.netty.msg.codc.MsgEncoder;
import com.ai.netty.msg.ss.SerializationUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketNettyClient {
    EventLoopGroup boss;
    EventLoopGroup worker;
    ClientHandler handler;
    Bootstrap bootstrap = null;

    public WebSocketNettyClient() {
        bootstrap = initServer();
    }


    public Bootstrap initServer() {
        Bootstrap bootstrap = new Bootstrap();
        boss = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(boss)
                .channel(NioSocketChannel.class)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public NioSocketChannel connect(String host, Integer port, OnSuccessFuture onSuccessFuture, String params) {
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline pipeline = channel.pipeline();
//                if (sslCtx != null) {
//                    pipeline.addLast(sslCtx.newHandler(channel.alloc(), host, port));
//                }
                pipeline.addLast(new HttpClientCodec(),
                        new HttpObjectAggregator(65536),
                        WebSocketClientCompressionHandler.INSTANCE,
                        new IdleStateHandler(60, 0, 0),
                        //"token=1&env=stg&type=1&datasetId=989"
                        new ClientHandler(generateUri(host, port, params)));
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync().addListener(onSuccessFuture::onFuture);
            return (NioSocketChannel) channelFuture.channel();
        } catch (InterruptedException e) {
//
            return null;
        }
    }


    private URI generateUri(String host, Integer port, String params){
        String url = "ws://" + host + ":" + port + "/websocket?" + params;
        return URI.create(url);
    }

    private URI generateUri(String host, Integer port){
        String url = "ws://" + host + ":" + port;
//        if (sslCtx == null) {
//            if (port < 1) {
//                port = 80;
//            }
//            url = "ws://" + host + ":" + port;
//        } else {
//            if (port < 1) {
//                port = 443;
//            }
//            url = "wss://" + host + ":" + port;
//        }
        return URI.create(url);
    }



    public void destroy() {
        if (boss != null && !boss.isShutdown()) {
            boss.shutdownGracefully();
        }
    }

    @FunctionalInterface
    public interface OnSuccessFuture {
        /**
         * onFuture
         * @param future future
         */
        void onFuture(Future<?> future);
    }

    // 连接成功的回调用
    public static void connectFuture(Future<?> future){
        if (future.isSuccess()) {
            log.info("连接成功!");
        }
    }
}
