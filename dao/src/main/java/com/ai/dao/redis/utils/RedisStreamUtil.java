package com.ai.dao.redis.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/04/19/22:50
 * @Description:
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamInfo.*;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RedisStreamUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;



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

    // 添加Map消息
    public String addMap(String key, Map<String, Object> value) {
        return redisTemplate.opsForStream().add(key, value).getValue();
    }

    // 读取消息
    public List<MapRecord<String, Object, Object>> read(String key) {
        return redisTemplate.opsForStream().read(StreamOffset.fromStart(key));
    }

    // 确认消费
    public Long ack(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    // 删除消息。当一个节点的所有消息都被删除，那么该节点会自动销毁
    public Long del(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    // 判断是否存在key
    public boolean hasKey(String key) {
        Boolean aBoolean = redisTemplate.hasKey(key);
        return aBoolean != null && aBoolean;
    }
}



