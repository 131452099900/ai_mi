package com.ai.dao.redis.listener;

import com.ai.dao.redis.utils.RedisStreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListenMsgStream implements StreamListener<String , ObjectRecord<String, MsgDemo> > {

    @Autowired
    private RedisStreamUtil redisStreamUtil;

    //一个监听消息解析队列，一个监听消息记录队列
    @Override
    public void onMessage(ObjectRecord<String, MsgDemo> message) {
        log.info("1--------------_>监听到一条消息");
        System.out.println(message);


        // 手动ack
        redisStreamUtil.ack(ListenerConfig.QUEUE1, ListenerConfig.QUEUE1_GROUP1, message.getId().getValue());
    }
}
