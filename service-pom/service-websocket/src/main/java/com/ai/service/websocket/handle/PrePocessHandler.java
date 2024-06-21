package com.ai.service.websocket.handle;

import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;

public class PrePocessHandler extends AbsMsgHandler{
    @Override
    public void doProcess(WsMessage wsMessage, WsSession wsSession) {
        // 发送端发送collection等信息，接收端接收，创建collection，并且初始化任务，返回新的collectionid
    }
}
