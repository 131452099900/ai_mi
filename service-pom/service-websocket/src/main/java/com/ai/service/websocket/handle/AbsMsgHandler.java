package com.ai.service.websocket.handle;

import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;

public abstract class AbsMsgHandler implements MsgHandler{

    @Override
    public void process(WsMessage wsMessage, WsSession wsSession) {
        doProcess(wsMessage, wsSession);
    }

    public abstract void doProcess(WsMessage wsMessage, WsSession wsSession);
}
