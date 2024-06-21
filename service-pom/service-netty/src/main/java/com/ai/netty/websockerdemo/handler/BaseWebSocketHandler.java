package com.ai.netty.websockerdemo.handler;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.ai.common.core.utils.JsonUtils;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.impl.ClientConnectAckMsg;
import com.ai.netty.websockerdemo.RequestEnum;
import com.ai.netty.websockerdemo.cache.AckQueue;
import com.ai.netty.websockerdemo.manager.SessionManager;
import com.ai.netty.websockerdemo.service.AuthServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.netty.handler.timeout.IdleState.WRITER_IDLE;

@Component
@Slf4j
public class BaseWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Autowired
    private AuthServiceImpl authService = new AuthServiceImpl();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof CloseWebSocketFrame) {
            ctx.channel().close();
        }

        if(msg instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
        }


        // 业务
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame serviceMsg = (TextWebSocketFrame) msg;
            ByteBuf buf = serviceMsg.content();
            String jsonString = buf.toString(StandardCharsets.UTF_8);

            MsgImpl msgImpl = JsonUtils.parseObject(jsonString, MsgImpl.class);

            int msgType = msgImpl.getMsgType();
            if (msgType == 0) {
                // client connect notify
                String data = msgImpl.getData();
                ClientConnectAckMsg clientConnectAckMsg = JsonUtils.parseObject(data, ClientConnectAckMsg.class);
                ctx.fireChannelRead(clientConnectAckMsg);
            } else if (msgType == 1) {
                // msgType = 1 ack连接包 取消ack连接

            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("{}", evt.getClass());

        if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {
            log.info("开始握手");
        }

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手心跳 认证
            AttributeKey<Integer> typeKey = AttributeKey.valueOf("type");
            AttributeKey<String> envKey = AttributeKey.valueOf("env");
            AttributeKey<String> cidKey = AttributeKey.valueOf("cid");

            Integer type = ctx.channel().attr(typeKey).get();
            String env  = ctx.channel().attr(envKey).get();
            String cid = ctx.channel().attr(cidKey).get();
            log.info("======================================++>");

            // 向服务端发起认证
            CompletableFuture<Boolean> futureResp = authService.authSync(type, cid);

            final Boolean isAuth = futureResp.get();
            if (isAuth) {

                // type = 1
                // 1,请求同步连接 请求ClientB接口
                // 2,cid channel

                if (type.equals(RequestEnum.REQUEST.getId())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sourceId", cid);
                    map.put("env", env);
                    NioSocketChannel channel = (NioSocketChannel)ctx.channel();
                    SessionManager.add(cid, channel);
                    String clientBUrl = HttpUtil.post("http://localhost:9996/syncConnect",  JsonUtils.toJsonString(map));
//
//                    String body = HttpRequest
//                            .post("http://localhost:9990/syncConnect")
//                            .header("Content-Type", "application/json")
//                            .body(JsonUtils.toJsonString(map))
//                            .execute().body();

                    log.info("2======> clientA完成连接{}", cid);
                } else if (type.equals(RequestEnum.REQUEST_RESPONSE.getId())) {
                    Thread.sleep(5000L);
                    // 源传输者ID
                    AttributeKey<String> ccidKey = AttributeKey.valueOf("ccid");
                    String ccid = ctx.channel().attr(ccidKey).get();
                    // 1,加入manager
                    SessionManager.add(cid, (NioSocketChannel) ctx.channel());
                    log.info("=======> clientB连接成功{}", cid);
                    // TODO 暂时不考虑消息可靠   后面使用队列代替
                    // 2,给clientA发送连接ACK包 msgType = 1
                    ClientConnectAckMsg clientConnectAckMsg = new ClientConnectAckMsg();
                    clientConnectAckMsg.setAckCcid(cid);
                    clientConnectAckMsg.setCid(ccid);
                    MsgImpl msg = MsgImpl.builder()
                            .version(1)
                            .msgType(1)
                            .from(cid)
                            .to(ccid)
                            .retryNum(0)
                            .sessionId(UUID.randomUUID().toString()).data(JsonUtils.toJsonString(clientConnectAckMsg)).build();
                    log.info("{}, {}", ccid, cid);
                    SessionManager.sendToOne(ccid, msg, clientConnectAckMsg);
                    log.info("=======> ClientB给ClientA发送connect ack包");

                    // 3,把该ack包加入队列
                    AckQueue.QUEUE.put(msg.getSessionId(), msg);
                }
            } else {
                ctx.writeAndFlush(new CloseWebSocketFrame(400, "token 无效")).addListener(ChannelFutureListener.CLOSE);
            }
//            futureResp.thenAccept(isAuth -> {
//                if (isAuth) {
//                    log.info("认证成功 {}, {}", token, type);
//
//                    // 向target发起连接请求，并且ack
//
//                    // 如果是client A 则需要通知client B，同时添加sessionManager
//                    if (type.equals(1)) {
//                        log.info("进来了");
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("sourceId", datasetId.toString());
//                        String postBody = HttpUtil.post("localhost:9990/syncConnect", map);
//                        log.info("connecting {} postBody", postBody);
//                        NioSocketChannel channel = (NioSocketChannel)ctx.channel();
//                        SessionManager.add(env + datasetId, channel);
//                    } else if (type.equals(2)) {
//                        // Client B响应连接的 给 Client A 发送特定ACK包
//
//                    }
//
//                } else {
//                    ctx.writeAndFlush(new CloseWebSocketFrame(400, "token 无效")).addListener(ChannelFutureListener.CLOSE);
//                }
//            });
        }




        // 向目标推送连接请求
        if(evt instanceof IdleStateEvent) {
            //将  evt 向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    log.info("读空闲");
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    log.info("写空闲");
                    break;
                case ALL_IDLE:
                    log.info("读写空闲");
                    eventType = "读写空闲";
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    private String getSourceEnv(String env) {
        if (env.equals("stg")) {
            return "test";
        }
        if (env.equals("prod")) {
            return "stg";
        }
        return "";

//        if (env.equals("prod")) {
//            return "stg";
//        } else if (env.equals("stg")) {
//            return "test";
//        } else {
//            return "dev";
//        }
    }
}
