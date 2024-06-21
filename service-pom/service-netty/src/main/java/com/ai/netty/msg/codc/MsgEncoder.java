package com.ai.netty.msg.codc;

import com.ai.netty.msg.MsgImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.util.SessionIdGeneratorBase;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MsgEncoder extends MessageToByteEncoder<MsgImpl> {

     // 1 (head) + 4 (version) + 1 (serviceType) + 1 (msgType) + 4 (logId) + 32 (sessionId)  + 4(dataLength) + data
    @Override
    protected void encode(ChannelHandlerContext ctx, MsgImpl msg, ByteBuf out) throws Exception {
        // 1
        out.writeByte(msg.getHead());
        // 5
        out.writeInt(msg.getVersion());
        // 6
        out.writeByte(msg.getServiceType());
        // 7
        out.writeByte(msg.getMsgType());
        // 11
        out.writeInt(msg.getLogId());
        // 47
        out.writeCharSequence(msg.getSessionId(), StandardCharsets.UTF_8);
        // a 5个字节
        out.writeInt(msg.getDataLength());
//        out.writeBytes(msg.getData());


    }
}
