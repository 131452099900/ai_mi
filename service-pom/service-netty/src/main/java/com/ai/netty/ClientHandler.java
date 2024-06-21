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
                //2024-06-20T17:39:35.582+08:00  INFO 51431 --- [ntLoopGroup-9-1] com.ai.netty.ClientHandler               : MsgImpl(head=0, version=1, serviceType=null, msgType=1, logId=0, sessionId=3f08e16b-4cd2-426e-a04c-ebc89c4bbffd, dataLength=0, from=prod_111, to=stg_111, data={"msgId":null,"type":1,"version":0,"to":null,"from":null,"ackCcid":"prod_111","cid":"stg_111"}, env=null, retryNum=0)
               // 17:39:36.000 [nioEventLoopGroup-2-1] INFO com.ai.netty.ClientHandler -- MsgImpl(head=0, version=2, serviceType=null, msgType=1, logId=0, sessionId=3f08e16b-4cd2-426e-a04c-ebc89c4bbffd, dataLength=0, from=prod_111, to=stg_111, data={"msgId":null,"type":1,"version":0,"to":null,"from":null,"ackCcid":"prod_111","cid":"stg_111"}, env=null, retryNum=1)

                int msgType = msgImpl.getMsgType();
                if (msgType == 1) {
                    // client connect notify
                    String data = msgImpl.getData();
                    ClientConnectAckMsg clientConnectAckMsg = JSONUtil.toBean(data, ClientConnectAckMsg.class);
                    log.info("======== clientA 接收connect ack成功");
                    // 5.1 设置任务
                    TaskState taskState = TaskCache.get(msgImpl.getTo());
                    taskState.setCcid(msgImpl.getFrom());
                    taskState.setState(2);
                    // 5.2 去除
                    ConnectAckQueue.QUEUE_CA.remove(msgImpl.getTo());
                    log.info("5.2 去除ack连接队列");
                    ctx.fireChannelRead(clientConnectAckMsg);

                    // 发送ack消息
                    MsgImpl.ack(msgImpl.getTo(), "server", null);
                } else if (msgType == 2) {

                }
            }
        } else   if (o instanceof CloseWebSocketFrame){
            System.out.println("WebSocketClientHandler::channelRead0 CloseWebSocketFrame");
        }
    }

}
