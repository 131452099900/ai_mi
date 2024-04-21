package com.ai.dao.redis;

import com.ai.BaseTest;
import com.ai.common.core.domain.dto.UserOnlineDTO;
import com.ai.dao.redis.listener.MsgDemo;
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
        final MsgDemo msgDemo = new MsgDemo();
        for (int i = 0; i < 20; i++) {
            msgDemo.setId(String.valueOf(i));
            redisStreamUtil.xadd("QUEUE1", msgDemo, MsgDemo.class);
        }

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
        List<MapRecord<String, Object, Object>> queueName = redisStreamUtil.xread("queueName");
//        [MapBackedRecord{recordId=1713540395997-0, kvMap={key1=value1}}]
        System.out.println(queueName);
    }

    @Test
    public void createByGroup() {
        System.out.println(redisStreamUtil.createGroup("queueName", "g2"));
    }
    @Test
    public void testGroup() {
        System.out.println(redisStreamUtil.queryGroups("queueName"));
    }

    @Test
    public void xaddByGroup() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("key", "value1");
        redisStreamUtil.xadd("queueName", hashMap);
    }

    @Test
    public void xreadByGroup() {
        System.out.println(redisStreamUtil.xreadOneByGroup("queueName", "g2", "c1"));
    }
    @Test
    public void xreadByGroup1() {
        System.out.println(redisStreamUtil.xreadOneByGroup("queueName", "g1", "c1"));
    }

    @Test
    public void testXaddObject() {
        UserOnlineDTO userOnlineDTO = new UserOnlineDTO();
        userOnlineDTO.setUserName("wwwww");
        final String q1 = redisStreamUtil.xadd("q1", userOnlineDTO, UserOnlineDTO.class);
        System.out.println(q1);
    }

    @Test
    public void testReadObject() {
        System.out.println(redisStreamUtil.xreadObject("q1", 1L, UserOnlineDTO.class));
    }

}
