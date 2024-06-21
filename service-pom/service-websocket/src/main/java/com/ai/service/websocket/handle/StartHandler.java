package com.ai.service.websocket.handle;

import com.ai.common.core.utils.JsonUtils;
import com.ai.service.websocket.model.WsMessage;
import com.ai.service.websocket.model.WsSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Slf4j
public class StartHandler extends AbsMsgHandler{
    @Override
    public void doProcess(WsMessage wsMessage, WsSession wsSession) {
        // start 接口到start 返回ack，响应任务Id
        log.info(wsMessage.getData());
        WsMessage wsMessage1 = new WsMessage();
        wsMessage1.setData("[ACK]");
        wsMessage1.setType(6);
        try {
            wsSession.getWebSocketSession().sendMessage(new TextMessage(JsonUtils.toJsonString(wsMessage1)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
