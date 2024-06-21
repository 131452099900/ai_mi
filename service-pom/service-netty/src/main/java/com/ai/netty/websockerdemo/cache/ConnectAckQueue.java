package com.ai.netty.websockerdemo.cache;

import com.ai.netty.msg.MsgImpl;
import com.ai.netty.websockerdemo.model.TaskState;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConnectAckQueue {
    public static volatile ConcurrentHashMap<String , MsgImpl> QUEUE_CA = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String , MsgImpl> QUEUE_CB = new ConcurrentHashMap<>();


    public static void run() throws InterruptedException {
        // 从队列中获取 只能根据 cid来辨认了
        while (true) {
            log.info("++++++++进行了一次");
            Set<Map.Entry<String, MsgImpl>> entries = QUEUE_CA.entrySet();
            for (Map.Entry<String, MsgImpl> entry : entries) {
                MsgImpl value = entry.getValue();
                if (value.getRetryNum() > 3) {
                    entries.remove(entry);

                    // 同时修改任务为-1状态
                    TaskState taskState = TaskCache.get(entry.getValue().getFrom());
                    taskState.setState(-1);
                    log.info("========> 抛弃连接 任务是 {}, {}", value, taskState);
                } else {
                    value.setRetryNum(value.getRetryNum() + 1);
                    log.info("{}", value);
                    // 是等待确认
                }
            }

            Thread.sleep(5000L);
        }

    }

}
