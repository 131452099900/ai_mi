package com.ai.netty;

import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.Task;
import com.ai.netty.websockerdemo.cache.AckQueueListener;
import com.ai.netty.websockerdemo.cache.ConnectAckQueue;
import com.ai.netty.websockerdemo.cache.TaskCache;
import com.ai.netty.websockerdemo.model.TaskState;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {


    public static void main(String[] args)  {
        WebSocketNettyClient client = new WebSocketNettyClient();
        NioSocketChannel a = client.connect("localhost", 6666, WebSocketNettyClient::connectFuture, buildParams());
        // 1,发起连接
        log.info("1.1 /ws clientA连接");

        // 2 任务初始化   若失败为0，若连接成功为2未开始，后面发送init包后为3传输中，4为完成

        TaskState task = new TaskState();
        task.setCid("stg_111");
        task.setDatasetId(1);
        task.setState(1);
        // 初始化任务
        TaskCache.put(task.getCid(), task);
        log.info("1.2 把stg_111任务初始化");

        // 3. acc连接确认队列
        MsgImpl msg = MsgImpl.builder().retryNum(0).env("stg").from(task.getCid()).build();
        ConnectAckQueue.QUEUE_CA.put(task.getCid(), msg);
        log.info("1.3 把stg_111连接ack队列");

        final Runnable runnable1 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                ConnectAckQueue.run();
            }
        };
        new Thread(runnable1).start();

        final Runnable runnable2 = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                TaskCache.run();
            }
        };
        new Thread(runnable2).start();

        final Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                AckQueueListener.run();
            }
        };
        new Thread(runnable).start();
    }

    private static String buildParams () {
        // type
        // env
        // cid
        String type = "type=1&";
        String env = "env=stg&";
        String cid = "cid=stg" + "_111";
        return type.concat(env).concat(cid);
    }
}
