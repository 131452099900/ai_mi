package com.ai.service.websocket.handle;

import com.ai.service.websocket.model.WsMessage;

public abstract class AbsMsgHandler implements MsgHandler{

    @Override
    public void process(WsMessage wsMessage) {
        doProcess(wsMessage);
    }

    public abstract void doProcess(WsMessage wsMessage);
}
