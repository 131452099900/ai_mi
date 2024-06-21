package com.ai.netty.buffer.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestEm {
    public static void main(String[] args) {
        final EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG)
        );

        sendData(embeddedChannel, "aaa");

    }

    private static void sendData(EmbeddedChannel channel, String msg) {
        final byte[] bytes = msg.getBytes();
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(msg.length());
        buffer.writeBytes(bytes);

        channel.writeInbound(buffer);
    }
}
