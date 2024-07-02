package com.ai.netty.server.handler;

import com.ai.netty.ClientHandler;
import com.ai.netty.websockerdemo.handler.ConnectionAckHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/06/22/11:36
 * @Description:
 */
public class MyServiceClientHandler {
    public static final List<SimpleChannelInboundHandler> serviceHandlers = new LinkedList<>();

    static {
        serviceHandlers.add(new ConnectionAckHandler());
    }
}
