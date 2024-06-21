package com.ai.service.websocket.manager;

import com.ai.service.websocket.model.WsSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionManager {
    private final static ConcurrentHashMap<String, WsSession> SESSION_MAP = new ConcurrentHashMap<>();

    public static void put (String key, WsSession wsSession) {
        SESSION_MAP.put(key, wsSession);
    }

    public static WsSession remove(String key) {
        return SESSION_MAP.remove(key);
    }

    public static WsSession get(String key) {
        return SESSION_MAP.get(key);
    }

    public void close(String key) {
        final WsSession remove = remove(key);
        try {
            remove.getWebSocketSession().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
