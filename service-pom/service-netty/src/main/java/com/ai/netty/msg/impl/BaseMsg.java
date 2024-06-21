package com.ai.netty.msg.impl;

import lombok.Data;

@Data
public class BaseMsg {
    String msgId;
    int type;
    int version;
    String to;
    String from;
}
