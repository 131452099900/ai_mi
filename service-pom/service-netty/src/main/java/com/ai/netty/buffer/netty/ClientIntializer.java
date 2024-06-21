package com.ai.netty.buffer.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class ClientIntializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // 3s未写触发读闲置
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast("", new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS))
                .addLast("heart", new HeartBeatCilentHandler());
    }
}
