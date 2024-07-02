package com.ai.netty.msg.impl;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/06/22/11:17
 * @Description:
 */
@Data
public class AckMsg extends BaseMsg{

    private String ackSessionId;

    public AckMsg() {
        super.type = -1;
    }
}
