package com.ai.dao.redis;

import com.ai.BaseTest;
import com.ai.dao.redis.utils.RedisStreamUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2024/04/19/23:09
 * @Description:
 */
public class StreamTest extends BaseTest {

    @Autowired
    private RedisStreamUtil redisStreamUtil;

    @Test
    public void testAdd() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("key1", "value1");
        redisStreamUtil.addMap("queueName", hashMap);
    }

    @Test
    public void testHasKey() {
        System.out.println(redisStreamUtil.hasKey("queueName"));
    }

    @Test
    public void testCreateGroup() {
        String group = redisStreamUtil.createGroup("queueName", "g1");
        System.out.println(group);
    }

    @Test
    public void read() {
        List<MapRecord<String, Object, Object>> queueName = redisStreamUtil.read("queueName");
//        [MapBackedRecord{recordId=1713540395997-0, kvMap={key1=value1}}]
        System.out.println(queueName);
    }

    @Test
    public void readByGroup() {
//        redisStreamUtil.read("queueName", "g1");

    }
}
