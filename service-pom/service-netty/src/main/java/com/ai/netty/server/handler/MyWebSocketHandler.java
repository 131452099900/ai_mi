package com.ai.netty.server.handler;

import com.ai.netty.msg.codc.MsgDecoder;
import com.ai.netty.msg.codc.MsgEncoder;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MyWebSocketHandler extends ChannelInitializer<SocketChannel> {
    @Autowired
    private WsParamsHandler wsParamsHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // handler链
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

        // 1 http编码解码器  websocket连接是基于http的三次握手的    每个线程独享
        pipeline.addLast(new HttpServerCodec());
        // 2 httpContent消息聚合，对于post的body聚合在httpContent  每个线程独享
        pipeline.addLast(new HttpObjectAggregator(65536));

        // 3 协议升级   websocket如果url带参数容易断开连接 /im/ws?w=221100234&t=99 像这样会在http导致3次握手失败
        pipeline.addLast("websocketParamHandler", new WsParamsHandler());

        // 4 websocket协议处理器 主要做了切换http编码解码器都websocket（协议升级），
        pipeline.addLast("protocolHandler",new WebSocketServerProtocolHandler("/websocket", true));

        // 5 心跳
//        pipeline.addLast(new IdleStateHandler(9, -1, -1, TimeUnit.SECONDS));

        pipeline.addLast("wsBaseHandler", new com.ai.netty.websockerdemo.handler.BaseWebSocketHandler());

        //
//        pipeline.addLast("msgHandler", new MsgHandler());
//        pipeline.addLast("BinaryWebSocketFrameHandler", new BinaryWebSocketFrameHandler());
    }
}
