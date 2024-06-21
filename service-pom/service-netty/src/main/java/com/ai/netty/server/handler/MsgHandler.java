package com.ai.netty.server.handler;

import com.ai.netty.msg.MsgImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgHandler extends SimpleChannelInboundHandler<MsgImpl> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgImpl msg) throws Exception {
        log.info("{}", msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("========++>");
        ctx.fireChannelActive();
    }


}
