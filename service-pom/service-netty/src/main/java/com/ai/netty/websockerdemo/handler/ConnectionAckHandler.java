package com.ai.netty.websockerdemo.handler;

import cn.hutool.json.JSONUtil;
import com.ai.common.core.utils.JsonUtils;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.impl.BaseMsg;
import com.ai.netty.msg.impl.ClientConnectAckMsg;
import com.ai.netty.msg.impl.InitMsg;
import com.ai.netty.websockerdemo.cache.ConnectAckQueue;
import com.ai.netty.websockerdemo.cache.TaskCache;
import com.ai.netty.websockerdemo.manager.SessionManager;
import com.ai.netty.websockerdemo.model.TaskState;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
public class ConnectionAckHandler extends SimpleChannelInboundHandler<ClientConnectAckMsg> {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientConnectAckMsg msg) throws Exception {
        System.out.println(msg);

        // 给server发送ack
        // 5.1 设置任务
        String cid = msg.getCid();
        String ccid = msg.getCcid();
        System.out.println(ccid);
        System.out.println(cid);
        System.out.println(msg.getSessionId());
        TaskState taskState = TaskCache.get(ccid);
        taskState.setCcid(cid);
        taskState.setState(2);
        // 5.2 去除
        ConnectAckQueue.QUEUE_CA.remove(ccid);
        log.info("5.2 去除ack连接队列");

        // 5.3 发送ack消息
        MsgImpl ackMsg = MsgImpl.ack(ccid, "server", msg.getSessionId());
        if (ctx.channel().isActive()) {
            log.info("发送消息了");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(ackMsg)));
        }

    }
}

