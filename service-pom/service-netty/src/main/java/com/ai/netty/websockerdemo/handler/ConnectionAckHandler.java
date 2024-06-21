package com.ai.netty.websockerdemo.handler;

import com.ai.common.core.utils.JsonUtils;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.impl.BaseMsg;
import com.ai.netty.msg.impl.ClientConnectAckMsg;
import com.ai.netty.msg.impl.InitMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.UUID;

public class ConnectionAckHandler extends SimpleChannelInboundHandler<ClientConnectAckMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientConnectAckMsg msg) throws Exception {
        // 给server发送ack
        String to = msg.getTo();
        MsgImpl ack = MsgImpl.ack(msg.getMsgId(),to);
        ctx.writeAndFlush(ack);

        // 1.改变connect ack的状态state

        // 2.给server发送ack包

        // 给client发送Init包
//        MsgImpl msg1 = new MsgImpl();
//        final InitMsg initMsg = new InitMsg();
//        msg1.setMsgType(2);
//        final String s = UUID.randomUUID().toString();
//        msg1.setSessionId(s);
//        msg1.setFrom(to);
//        msg1.setTo(msg.getFrom());
//        initMsg.setDatasetId(111L);
//        initMsg.setVersion(1);
//        initMsg.setMsgId(s);
//        initMsg.setCount(1000);
//        initMsg.setPartitions(new ArrayList<>());
//
//        msg1.setData(JSON.parseObject(JsonUtils.toJsonString(initMsg)));
//        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtils.toJsonString(initMsg)));
    }
}

