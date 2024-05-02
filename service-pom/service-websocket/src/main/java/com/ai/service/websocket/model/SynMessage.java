package com.ai.service.websocket.model;

public class SynMessage {
    // 0 start 1 data 2 end 3ping 4pong 5preData    6ack
    private Integer type;
    private Object data;
}
