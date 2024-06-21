package com.ai.service.websocket.model;

public class SynMessage {
    // 0 start 1 data 2 end 3 ping 4pong 5preData    6 ack
    private Integer type;
    private Object data;
}
