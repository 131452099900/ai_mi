package com.ai.netty;

import com.ai.netty.msg.MsgImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WebsocketClient {

    private final static Logger logger = LoggerFactory.getLogger(WebsocketClient.class);

    private NioEventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private SslContext sslCtx;

    public WebsocketClient(SslContext sslCtx) {
        this.sslCtx = sslCtx;
        bootstrap = initBootstrap();
    }

    public WebsocketClient(){
        bootstrap = initBootstrap();
    }

    public Bootstrap initBootstrap(){
        // 1个NioEventLoop专门接收请求
        workerGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public NioSocketChannel connect(String host, Integer port, OnSuccessFuture onSuccessFuture) {
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                if (sslCtx != null) {
                    pipeline.addLast(sslCtx.newHandler(channel.alloc(), host, port));
                }
                pipeline.addLast(new HttpClientCodec(),
                        new HttpObjectAggregator(65536),
                        WebSocketClientCompressionHandler.INSTANCE,
                        new IdleStateHandler(60, 0, 0),
                        new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {

                            }
                        }
                );
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync().addListener(onSuccessFuture::onFuture);
            return (NioSocketChannel) channelFuture.channel();
        } catch (InterruptedException e) {
            logger.error("error", e);
            return null;
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

    private URI generateUri(String host, Integer port){
        String url;
        if (sslCtx == null) {
            if (port < 1) {
                port = 80;
            }
            url = "ws://" + host + ":" + port;
        } else {
            if (port < 1) {
                port = 443;
            }
            url = "wss://" + host + ":" + port;
        }
        return URI.create(url);
    }

    public void destroy() {
        if (workerGroup != null && !workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
    }
}

