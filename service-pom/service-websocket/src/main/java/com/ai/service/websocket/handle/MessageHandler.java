package com.ai.service.websocket.handle;

import cn.hutool.core.util.ObjectUtil;
import com.ai.common.core.utils.JsonUtils;
import com.ai.dao.redis.listener.ListenerConfig;
import com.ai.dao.redis.listener.MsgDemo;
import com.ai.dao.redis.utils.RedisStreamUtil;
import com.ai.service.websocket.manager.SessionManager;
import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;

/**
 * websocket处理器
 */
@Component
@Slf4j
public class MessageHandler extends TextWebSocketHandler {


    @Autowired
    private RedisStreamUtil redisStreamUtil;

    @Value("${server.name}")
    private String name;
    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = (String) session.getAttributes().get("token");
        WsSession wsSession = new WsSession();
        wsSession.setWebSocketSession(session);
        String s = UUID.randomUUID().toString();
        wsSession.setSessionId(s);
        wsSession.setToken(token);
        wsSession.setToken((String) session.getAttributes().get("token"));
        SessionManager.put(session.getId(), wsSession);
        log.info("======> 连接建立后");
    }



    /**
     * 消息处理
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {
        log.info("======> 消息处理" + message.getPayload());
        WsMessage wsMessage = JsonUtils.parseObject(message.getPayload(), WsMessage.class);
        WsSession wsSession = SessionManager.get(session.getId());
        final MsgDemo msgDemo = new MsgDemo();
        msgDemo.setId(message.getPayload());
        msgDemo.setOrigin(name);
        redisStreamUtil.xadd(ListenerConfig.WEBSOCKET_QUEUE, msgDemo ,MsgDemo.class);

        if (ObjectUtil.isNull(wsMessage)) {
//            final MsgDemo msgDemo = new MsgDemo();
//            msgDemo.setId(message.getPayload());
//            redisStreamUtil.xadd(ListenerConfig.WEBSOCKET_QUEUE, msgDemo ,MsgDemo.class);
        } else {
            final WsMessage wsMessage1 = new WsMessage();
            wsMessage1.setData("返回数据");
            wsMessage1.setType(2);
            final String jsonString = JsonUtils.toJsonString(wsMessage1);
            try {
                wsSession.getWebSocketSession().sendMessage(new TextMessage(jsonString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 心跳处理
     */
    protected void handlePongMessage(WebSocketSession session, PongMessage message)  {
        log.info("======> 连接建立后" + message.toString());
    }

    /**
     * 运输报错
     */
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("======> 运输报错");
    }

    /**
     * 连接关闭
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("======> 连接关闭");
    }

}
