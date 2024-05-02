package com.ai.service.websocket.handle;

import com.ai.dao.redis.listener.MsgDemo;
import com.ai.service.websocket.model.WsMessage;

public interface MsgHandler  {
    public void process(WsMessage message);
}
