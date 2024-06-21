package com.ai.netty.server.handler;

import cn.hutool.json.JSONUtil;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.ss.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BaseWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    int i = 0;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // netty的websocket存在6种内部的数据帧
        System.out.println(frame.getClass() + "-------------————》");
        if(frame instanceof CloseWebSocketFrame) {
            log.info("=========> 连接关闭");
            ctx.channel().close();
        }

        //        判断是否时ping消息
        if(frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }

        if(frame instanceof PongWebSocketFrame) {
            log.info("接收到Pong包");
        }


        //      判断是否是二进制消息，如果是二进制消息，就抛出异常
        if(frame instanceof BinaryWebSocketFrame) {
            ByteBuf content = frame.content();
            byte[] bytes = new byte[content.capacity()];
            content.readBytes(bytes);
            MsgImpl deserialize = (MsgImpl) SerializationUtil.deserialize(bytes);
            System.out.println(deserialize);
        }

        if (frame instanceof TextWebSocketFrame) {
            String s = frame.content().toString(StandardCharsets.UTF_8);
            MsgImpl msg = JSONUtil.toBean(s, MsgImpl.class);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        i++;
        log.info("{}", evt.getClass());

        if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {
            log.info("开始握手");
        }

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("完成握手 开始认证");
            AttributeKey<String> attributeKey = AttributeKey.valueOf("token");
            String token = ctx.channel().attr(attributeKey).get();
            // 发起请求校验 token判断user的有效信息
            System.out.println("token is " + token);
        }

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case WRITER_IDLE:
                    log.info("写时间-------->");
                    break;
                case READER_IDLE:
                    System.out.println(ctx.channel().id());
                    log.info("读时间-------->");
                    break;
                case ALL_IDLE:
                    log.info("全时间-------->");
            }
        }
    }

}
