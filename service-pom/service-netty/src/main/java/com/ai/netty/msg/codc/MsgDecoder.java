package com.ai.netty.msg.codc;

import com.ai.netty.msg.MsgImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        // 1 (head) + 4 (version) + 1 (serviceType) + 1 (msgType) + 4 (logId) + 32 (sessionId)  + 4(dataLength) + data
//        byte head = in.readByte();
//        int version = in.readInt();
//        int serviceType = in.readByte();
//        byte msgType = in.readByte();
//        int logId = in.readInt();
//        String charSequence = (String)in.readCharSequence(32, StandardCharsets.UTF_8);
//        int dataLength = in.readInt();
//        ByteBuf byteBuf = in.readBytes(dataLength);
//        String s = byteBuf.toString(StandardCharsets.UTF_8);
//        System.out.println(s);
//        log.info("head is {}, version is {}, serviceType is {}", head, version, serviceType);
//        log.info("msgType is {}, logId is {}, char is {}, dataL is {}", msgType, logId, charSequence, dataLength);
//        log.info("data is {}", s);
//
//        MsgImpl protocol = new MsgImpl();
//        protocol.setVersion(version);
//        protocol.setServiceType(serviceType);
//        protocol.setMsgType(msgType);
//        protocol.setLogId(logId);
//        protocol.setSessionId(s);
        // 网关不反序列化，反序列化步骤在service做
//        protocol.setData(byteBuf.array());

//        out.add(protocol);
    }
}
