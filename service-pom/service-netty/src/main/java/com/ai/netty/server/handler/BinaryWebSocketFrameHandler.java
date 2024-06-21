package com.ai.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinaryWebSocketFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        log.info("服务器接收到二进制消息,消息长度:[{}]", msg.content().capacity());

    }
}
