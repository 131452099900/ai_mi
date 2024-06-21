package com.ai.netty.buffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        final SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(9991));
        sc.write(Charset.defaultCharset().encode("中国"));

    }
}
