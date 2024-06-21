package com.ai.netty.websockerdemo.cache;

import com.ai.netty.msg.MsgImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AckQueue {
    public static volatile ConcurrentHashMap<String ,MsgImpl> QUEUE = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String ,MsgImpl> QUEUE_CA = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String ,MsgImpl> QUEUE_CB = new ConcurrentHashMap<>();
    public static void add(MsgImpl msg) {
        QUEUE.put(msg.getSessionId(), msg);
    }

    public static void remove(String id) {
        QUEUE.remove(id);
    }

}
