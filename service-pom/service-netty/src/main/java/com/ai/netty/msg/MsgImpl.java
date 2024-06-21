package com.ai.netty.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import com.alibaba.fastjson.JSONObject;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgImpl implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 1 (head) + 4 (version) + 1 (serviceType) + 1 (msgType) + 4 (logId) + 3 (sessionId)  + 4(dataLength) + data
    private byte head = 0X33;

    // 版本
    private int version;

    // 业务类型 1 发送init数据包 2 发送正常数据包 3发送close数据包   这些都是request类型的
    // 0 Client B 连接成功通知
    private Integer serviceType;

    // 消息类型 1request 2ack 3notify
    private int msgType;

    private int logId;

    private String sessionId;

    private int dataLength;

    private String from;

    private String to;

    private String data;

    private String env;

    private Integer retryNum;

    public static MsgImpl ack(String msgId,String from) {
        MsgImpl msg = MsgImpl.builder()
                .data(msgId)
                .sessionId(UUID.randomUUID().toString())
                .msgType(-1)
                .build();
        return msg;
    }



}
