package com.ai.netty.websockerdemo.cache;

import com.ai.netty.msg.Task;
import com.ai.netty.websockerdemo.model.TaskState;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TaskCache {
    private static volatile ConcurrentHashMap<String, TaskState> MAP = new ConcurrentHashMap<>();


    public static void put(String cid, TaskState task) {
        MAP.put(cid, task);
    }

    public static TaskState get(String cid) {
        return MAP.get(cid);
    }



    public static void run() throws InterruptedException {
        while (true) {
            for (Map.Entry<String, TaskState> entry : MAP.entrySet()) {
                if (entry.getValue().getState().equals(-1)) {
                    log.warn("任务失败  +++++>>>>>> {}", entry.getValue());
                    MAP.remove(entry.getKey());
                }
            }

            Thread.sleep(5000L);
        }

    }

}
