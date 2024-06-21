package com.ai.netty.msg.impl;


import lombok.Data;

// type = 1 发起连接 client B ack
@Data
public class ClientConnectAckMsg extends BaseMsg{
    String ackCcid;
    String cid;
    public ClientConnectAckMsg() {
        super.type = 1;
    }
}
