package com.ai.netty.buffer.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 闲置事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            // 写闲置
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.debug("关闭客户端连接");
                ctx.channel().close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果收到的是心跳包，则给客户端做出一个回复
        if ("I am Alive".equals(msg)){
            ctx.channel().writeAndFlush("I know");
        }
        System.out.println("收到客户端消息：" + msg);
        super.channelRead(ctx, msg);
    }
}
