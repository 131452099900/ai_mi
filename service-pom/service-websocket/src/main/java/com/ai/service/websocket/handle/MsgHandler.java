package com.ai.service.websocket.handle;

import com.ai.dao.redis.listener.MsgDemo;
import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;

public interface MsgHandler  {
    public void process(WsMessage message, WsSession wsSession);
}
