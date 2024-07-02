package com.ai.netty;

import cn.hutool.json.JSONUtil;
import com.ai.common.core.utils.JsonUtils;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.impl.ClientConnectAckMsg;
import com.ai.netty.websockerdemo.cache.AckQueue;
import com.ai.netty.websockerdemo.cache.ConnectAckQueue;
import com.ai.netty.websockerdemo.cache.TaskCache;
import com.ai.netty.websockerdemo.model.TaskState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.timeout.IdleState.WRITER_IDLE;


//客户端业务处理类
@Slf4j
public class ClientHandler   extends SimpleChannelInboundHandler<Object> {
    ChannelPromise handshakeFuture;


    private final URI uri;

    // 握手
    private WebSocketClientHandshaker handshake;

    public ClientHandler(URI uri) {
        this.uri = uri;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       log.info("======> trigger");
       log.info("{}", evt.getClass());
    }
    
    /**
     * 当客户端主动链接服务端的链接后，调用此方法
     *
     * @param channelHandlerContext ChannelHandlerContext
     */
    @SneakyThrows
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        System.out.println("客户端Active .....");
        handshake = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V00, null, true, new DefaultHttpHeaders());
        handshake.handshake(channelHandlerContext.channel());
        log.info("{}", handshake.isHandshakeComplete());
        handlerAdded(channelHandlerContext);
    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        System.out.println("\n\t⌜⎓⎓⎓⎓⎓⎓exception⎓⎓⎓⎓⎓⎓⎓⎓⎓\n" +
//                cause.getMessage());
//        ctx.close();
//    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        log.info("===============================+> {}", o.getClass());
        // 握手协议返回，设置结束握手
        if (!this.handshake.isHandshakeComplete()){
            FullHttpResponse response = (FullHttpResponse)o;
            this.handshake.finishHandshake(ctx.channel(), response);
            this.handshakeFuture.setSuccess();
            System.out.println("WebSocketClientHandler::channelRead0 HandshakeComplete...");
            return;
        }
        else  if (o instanceof TextWebSocketFrame)
        {
            TextWebSocketFrame msg = (TextWebSocketFrame)o;
            log.info("来了TextWebSocketFrame消息啦 {}", msg.text());

            // 业务
            if (msg instanceof TextWebSocketFrame) {
                TextWebSocketFrame serviceMsg = (TextWebSocketFrame) msg;
                ByteBuf buf = serviceMsg.content();
                String jsonString = buf.toString(StandardCharsets.UTF_8);

                MsgImpl msgImpl = JSONUtil.toBean(jsonString, MsgImpl.class);
                log.info("{}", msgImpl);

                int msgType = msgImpl.getMsgType();
                if (msgType == 1) {
                    // client connect notify
                    String data = msgImpl.getData();
                    ClientConnectAckMsg clientConnectAckMsg = JSONUtil.toBean(data, ClientConnectAckMsg.class);
                    clientConnectAckMsg.setSessionId(msgImpl.getSessionId());
                    log.info("======== clientA 接收connect ack成功 {}", clientConnectAckMsg);
                    ctx.fireChannelRead(clientConnectAckMsg);
                } else if (msgType == 2) {

                } else if (msgType == -1) {
                    // client端确认消息

                }
            }
        } else   if (o instanceof CloseWebSocketFrame){
            System.out.println("WebSocketClientHandler::channelRead0 CloseWebSocketFrame");
        }
    }

}
