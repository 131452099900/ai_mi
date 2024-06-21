package com.ai.service.websocket.handle;

import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;

public class PingHandler extends AbsMsgHandler{
    @Override
    public void doProcess(WsMessage wsMessage, WsSession wsSession) {
        // 返回pong
    }
}
