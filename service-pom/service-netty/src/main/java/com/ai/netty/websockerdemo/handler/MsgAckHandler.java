package com.ai.netty.websockerdemo.handler;

import com.ai.netty.msg.impl.AckMsg;
import com.ai.netty.websockerdemo.cache.AckQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/06/22/11:16
 * @Description:
 */
@Slf4j
@Component
public class MsgAckHandler extends SimpleChannelInboundHandler<AckMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AckMsg ackMsg) throws Exception {
        log.info("========================> kaishiwanc ");
        AckQueue.remove(ackMsg.getAckSessionId());
    }
}
