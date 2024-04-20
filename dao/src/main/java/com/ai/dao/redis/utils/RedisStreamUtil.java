package com.ai.dao.redis.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.connection.stream.StreamInfo.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class RedisStreamUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 添加消息
     * XADD xx * k1 v1 k2 v2
     */
    public String xadd(String key, Map<String, Object> record) {
        RecordId recordId = redisTemplate.opsForStream().add(key, record);
        return recordId.getValue();
    }

    /**
     * ObjectRecord<String, Book> record = StreamRecords.newRecord()
     *                 .in(streamKey)
     *                 .ofObject(book)
     *                 .withId(RecordId.autoGenerate());
     */
    public <T> String xadd(String key, T t, Class<T> clazz) {
        ObjectRecord<String, T> record = StreamRecords.newRecord()
                .in(key)
                .ofObject(t)
                .withId(RecordId.autoGenerate());
        RecordId recordId = redisTemplate.opsForStream().add(record);
        return recordId.getValue();
    }


    /**
     *  获取消息队列长度
     */
    public Long xLen(String key) {
        return redisTemplate.opsForStream().size(key);
    }

    /**
     * 顺序返回全部
     */
    public List<MapRecord<String, Object, Object>> xrange(String key) {
        List<MapRecord<String, Object, Object>> res = redisTemplate.opsForStream().range(key, Range.unbounded());
        return res;
    }

    /**
     * 顺序限制数量返回 XRANGE STREAM_NAME start end [COUNT count]
     */
    public List<MapRecord<String, Object, Object>> xrange(String key, Integer count) {
        List<MapRecord<String, Object, Object>> res = redisTemplate.opsForStream().range(key, Range.unbounded(), Limit.limit().count(count));
        return res;
    }

    /**
     * 顺序返回全部
     */
    public List<MapRecord<String, Object, Object>> reverseRange(String key) {
        List<MapRecord<String, Object, Object>> res = redisTemplate.opsForStream().reverseRange(key, Range.unbounded());
        return res;
    }

    /**
     * 顺序限制数量返回 XREVRANGE STREAM_NAME end start [COUNT count]
     */
    public List<MapRecord<String, Object, Object>> reverseRange(String key, Integer count) {
        List<MapRecord<String, Object, Object>> res = redisTemplate.opsForStream().reverseRange(key, Range.unbounded(), Limit.limit().count(count));
        return res;
    }

    /**
     * 裁剪数据
     * XTRIM STREAM_NAME MAXLEN [~] len
     * @Pamam count 限制队列的长度为count，先进先出
     */
    public Long trim(String key, Long count) {
        return redisTemplate.opsForStream().trim(key, count);
    }

    public Long xdel(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    public <T>  List<ObjectRecord<String, T>> xreadObject(String key, Class<T> clazz) {
        List<ObjectRecord<String, T>> objectRecords = xreadObject(key, 1L, clazz);
        return objectRecords;
    }
    public <T>  List<ObjectRecord<String, T>> xreadObject(String key, Long count, Class<T> clazz) {
       List<ObjectRecord<String, T>> read = redisTemplate.opsForStream().read(clazz, StreamReadOptions.empty().count(count), StreamOffset.fromStart(key));
        return read;
    }
    public <T> List<ObjectRecord<String, T>> xreadObjectGroup(String key, String group, String consumerName, Class<T> clazz) {
        return xreadObjectGroup(key, group, consumerName, clazz);
    }
    public <T> List<ObjectRecord<String, T>> xreadObjectGroup(String key, String group, String consumerName ,Long count, Class<T> clazz) {
        Consumer consumer = Consumer.from(group, consumerName);
        List<ObjectRecord<String, T>> res = redisTemplate.opsForStream().read(clazz, consumer, StreamReadOptions.empty().count(count), StreamOffset.create(key, ReadOffset.lastConsumed()));
        return res;
    }

    public <T> List<ObjectRecord<String, T>> xreadObjectGroup(String key, String group, String consumerName ,Long count, Long blockTime, Class<T> clazz) {
        Consumer consumer = Consumer.from(group, consumerName);
        List<ObjectRecord<String, T>> res = redisTemplate.opsForStream().read(clazz, consumer, StreamReadOptions.empty().count(count).block(Duration.ofMillis(blockTime)), StreamOffset.create(key, ReadOffset.lastConsumed()));
        return res;
    }

    public List<MapRecord<String, Object, Object>> xread(String key) {
        return xread(key, 1L);
    }

    /**
     * XREAD [COUNT count] [BLOCK milliseconds] STREAMS key [key ...] id [id ...]
     */
    public List<MapRecord<String, Object, Object>> xread(String key, Long count) {
        List<MapRecord<String, Object, Object>> res = redisTemplate.opsForStream().read(StreamReadOptions.empty().count(count), StreamOffset.fromStart(key));
        return res;
    }

    public  List<MapRecord<String, Object, Object>> blockRead(String key, Long count, Long blockTime) {
        List<MapRecord<String, Object, Object>> read = redisTemplate.opsForStream().read(StreamReadOptions.empty().block(Duration.ofMillis(blockTime)).count(count), StreamOffset.fromStart(key));
        return read;
    }

    public  List<MapRecord<String, Object, Object>> xreadGroupOneByNoBlock(String key, String group, String consumerName) {
        return xreadGroup(key, group, consumerName, 1L);
    }

    public  List<MapRecord<String, Object, Object>> xreadGroupOneByBlock(String key, String group, String consumerName, Long blockTime) {
        return xreadGroup(key, group, consumerName, 1L, blockTime);
    }

    public  List<MapRecord<String, Object, Object>> xreadGroup(String key, String group, String consumerName ,Long count, Long blockTime) {
        Consumer consumer = Consumer.from(group, consumerName);
        List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().count(count).block(Duration.ofMillis(blockTime)), StreamOffset.create(key, ReadOffset.lastConsumed()));
        return list;
    }

    public  List<MapRecord<String, Object, Object>> xreadGroup(String key, String group, String consumerName ,Long count) {
        Consumer consumer = Consumer.from(group, consumerName);
        List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(consumer, StreamReadOptions.empty().count(count), StreamOffset.create(key, ReadOffset.lastConsumed()));
        return list;
    }

    public  List<MapRecord<String, Object, Object>> xreadOneByGroup(String key, String group, String consumerName) {
        return  xreadGroup(key, group, consumerName, 1L);
    }


    /**
     * 创建消费组
     *
     * @param key   键名称
     * @param group 组名称
     * @return {@link String}
     */
    public String createGroup(String key, String group) {
        return redisTemplate.opsForStream().createGroup(key, group);
    }

    /**
     * 获取消费者信息
     *
     * @param key   键名称
     * @param group 组名称
     */
    public XInfoConsumers queryConsumers(String key, String group) {
        return redisTemplate.opsForStream().consumers(key, group);
    }

    /**
     * 查询组信息
     *
     * @param key 键名称
     * @return
     */
    public XInfoGroups queryGroups(String key) {
        return redisTemplate.opsForStream().groups(key);
    }


    // 确认消费
    public Long ack(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    // 判断是否存在key
    public boolean hasKey(String key) {
        Boolean aBoolean = redisTemplate.hasKey(key);
        return aBoolean != null && aBoolean;
    }

    /**
     * pending key group - + count consumer
     */
    public PendingMessages pending(String key, String group, Long count, String consumer) {
        return redisTemplate.opsForStream().pending(key, Consumer.from(group, consumer), Range.unbounded(), count);
    }

    public PendingMessages pending(String key, String group, Long count) {
        return redisTemplate.opsForStream().pending(key, group, Range.unbounded(), count);
    }

    // 显示待处理的消息：PendingMessagesSummary{
    public PendingMessagesSummary pending(String key, String group) {
        PendingMessagesSummary pending = redisTemplate.opsForStream().pending(key, group);
        return pending;
    }
}



