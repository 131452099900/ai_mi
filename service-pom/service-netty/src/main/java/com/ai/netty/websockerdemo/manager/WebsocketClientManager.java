package com.ai.netty.websockerdemo.manager;

import com.ai.netty.ClientHandler;
import com.ai.netty.WebSocketNettyClient;
import io.netty.channel.ChannelId;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class WebsocketClientManager extends BaseClientManager{
    private SslContext sslContext;
    private WebSocketNettyClient client;
    private static WebsocketClientManager INSTANCE;

    public static WebsocketClientManager getInstance() {
        return INSTANCE;
    }

    /**
     * 启动
     */
    @Override
    public boolean connect(String host, Integer port, String params) {
        channel = client.connect(host, port, this::connectFuture, params);
        final ChannelId id = channel.id();
        System.out.println(id.toString());
        this.host = host;
        this.port = port;
        return true;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        client.destroy();
    }

    @Override
    public void reconnect() {
        // Todo
        channel = client.connect(host, port, this::connectFuture, null);
    }

    public WebsocketClientManager() {
    }

    public static class Builder {
        private int sendDuration;
        private int sendRetry;
        private int connectRetry;

        public Builder() {
        }


        public Builder setSendDuration(int sendDuration) {
            this.sendDuration = sendDuration;
            return this;
        }


        public Builder setSendRetry(int sendRetry) {
            this.sendRetry = sendRetry;
            return this;
        }


        public Builder setConnectRetry(int connectRetry) {
            this.connectRetry = connectRetry;
            return this;
        }

        public WebsocketClientManager build(boolean ssl) throws Exception {
            WebsocketClientManager clientStarter = new WebsocketClientManager();
            clientStarter.sendDuration = sendDuration;
            clientStarter.sendRetry = sendRetry;
            clientStarter.connectRetry = connectRetry;
            clientStarter.sslContext = null;
            if(ssl){
                try {
                    clientStarter.sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                }catch (Exception e){
                    throw new Exception("初始ssl加密通道错误");
                }
            }
            clientStarter.client = new WebSocketNettyClient();
            WebsocketClientManager.INSTANCE = clientStarter;
            return clientStarter;
        }
    }
}

