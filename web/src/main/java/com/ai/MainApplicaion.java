package com.ai;

import com.ai.netty.Server;
import com.ai.netty.websockerdemo.cache.AckQueueListener;
import com.ai.netty.websockerdemo.cache.ConnectAckQueue;
import com.ai.netty.websockerdemo.cache.TaskCache;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
public class MainApplicaion {
    public static void main(String[] args) throws InterruptedException {
        new SpringApplication().run(MainApplicaion.class);
        new Server().run();

        final Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                AckQueueListener.run();
            }
        };
        new Thread(runnable).start();

        final Runnable runnable2 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                TaskCache.run();
            }
        };
        new Thread(runnable2).start();
    }
}
