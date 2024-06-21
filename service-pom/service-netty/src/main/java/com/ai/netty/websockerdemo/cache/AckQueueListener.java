package com.ai.netty.websockerdemo.cache;


import com.ai.netty.msg.MsgImpl;
import com.ai.netty.websockerdemo.manager.SessionManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class AckQueueListener {
    public static void run() throws InterruptedException {
        while (true) {
//            log.info("==============> listener run");
            final Set<Map.Entry<String, MsgImpl>> entries = AckQueue.QUEUE.entrySet();
            for (Map.Entry<String, MsgImpl> entry : entries) {
                MsgImpl value = entry.getValue();
                if (value.getRetryNum() > 3) {
                    String key = entry.getKey();
                    log.warn("server 一条key 为{}的消息被抛弃啦～", key);
                    AckQueue.QUEUE.remove(key);
                } else {
                    value.setRetryNum(value.getRetryNum() + 1);
                    // 重放
                    String to = value.getTo();
                    SessionManager.sendToOne(to, value, null);
                }
            }

            for (Map.Entry<String, MsgImpl> entry : AckQueue.QUEUE_CA.entrySet()) {
                MsgImpl value = entry.getValue();
                if (value.getRetryNum() > 3) {
                    String key = entry.getKey();
                    log.warn("clientA 一条key 为{}的消息被抛弃啦～", key);
                    AckQueue.QUEUE.remove(key);
                } else {
                    value.setRetryNum(value.getRetryNum() + 1);
                    String to = value.getTo();
                    SessionManager.sendToOne(to, value, null);
                }
            }

            for (Map.Entry<String, MsgImpl> entry : AckQueue.QUEUE_CB.entrySet()) {
                MsgImpl value = entry.getValue();
                if (value.getRetryNum() > 3) {
                    String key = entry.getKey();
                    log.warn("clientB 一条key 为{}的消息被抛弃啦～", key);
                    AckQueue.QUEUE.remove(key);
                } else {
                    value.setRetryNum(value.getRetryNum() + 1);
                    String to = value.getTo();
                    SessionManager.sendToOne(to, value, null);
                }
            }

            Thread.sleep(3000L);
        }
    }
}
