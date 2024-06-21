package com.ai.netty.buffer.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatCilentHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HRART_DATA = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("[PING]", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 闲置事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 写闲置
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(HRART_DATA.duplicate());
                log.debug("心跳包");
                Thread.sleep(6000L);
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("服务端关闭连接");
        super.channelInactive(ctx);
    }
}
