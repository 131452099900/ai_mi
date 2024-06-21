package com.ai.service.websocket.handle;

import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;

public class EndHandler extends AbsMsgHandler {

    @Override
    public void doProcess(WsMessage wsMessage, WsSession wsSession) {
        // 标记任务结束
    }
}
