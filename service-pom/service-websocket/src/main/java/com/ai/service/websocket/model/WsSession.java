package com.ai.service.websocket.model;

import com.ai.common.core.domain.model.LoginUser;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class WsSession {
    private WebSocketSession webSocketSession;
    private boolean isEnable;
    private LoginUser loginUser;
    private String sessionId;
    private String token;
}
