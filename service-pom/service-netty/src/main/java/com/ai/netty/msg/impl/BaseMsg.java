package com.ai.netty.msg.impl;

import lombok.Data;

@Data
public class BaseMsg {
    String sessionId;
    int type;
    int version;
    String cid;
    String ccid;
}
