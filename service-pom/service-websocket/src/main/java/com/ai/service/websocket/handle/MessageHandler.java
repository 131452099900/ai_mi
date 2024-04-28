package com.ai.service.websocket.handle;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * websocket处理器
 */
@Component
public class MessageHandler extends TextWebSocketHandler {

    /**
     * 连接建立后
     */
    public void afterConnectionEstablished(WebSocketSession session) {
    }


    /**
     * 消息处理
     */
    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {

    }


    /**
     * 心跳处理
     */
    protected void handlePongMessage(WebSocketSession session, PongMessage message)  {
    }

    /**
     * 运输报错
     */
    public void handleTransportError(WebSocketSession session, Throwable exception) {
    }

    /**
     * 连接关闭
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    }

}
