package com.ai.dao.redis;

import cn.hutool.extra.spring.SpringUtil;
import com.ai.BaseTest;
import com.ai.common.core.domain.dto.UserOnlineDTO;
import com.ai.dao.mybatis.mapper.TestDemoMapper;
import com.ai.dao.redis.listener.ListenerConfig;
import com.ai.dao.redis.listener.MsgDemo;
import com.ai.dao.redis.utils.RedisStreamUtil;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

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
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void t() {
        System.out.println(platformTransactionManager);
        System.out.println(transactionTemplate);
        System.out.println(transactionTemplate.getTransactionManager());
        final TransactionStatus transaction = transactionTemplate.getTransactionManager().getTransaction(new DefaultTransactionDefinition());
//        transactionTemplate.getTransactionManager()I.commit();
    }

//    @Autowired
    private TestDemoMapper testDemoMapper;
    @Test
    public void a() {
        System.out.println(testDemoMapper.selectList());
    }

    @Autowired
    private RedisStreamUtil redisStreamUtil;

    @Test
    public void testAdd() throws InterruptedException {

        for (int i = 0; i < 5; i++) {
            MsgDemo msgDemo = new MsgDemo();
            msgDemo.setId(String.valueOf(i));
            System.out.println(i);
            redisStreamUtil.xadd(ListenerConfig.QUEUE1, msgDemo, MsgDemo.class);

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
        List<ObjectRecord<String, MsgDemo>> objectRecords = redisStreamUtil.xreadObjectGroup(ListenerConfig.QUEUE1, ListenerConfig.QUEUE1_GROUP2, ListenerConfig.QUEUE1_GROUP2_CONSUMER1, MsgDemo.class);
        System.out.println(objectRecords);
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
        String key = "jhasgxasxgjasx";
        redisStreamUtil.initStream(key, "g1");
        redisStreamUtil.initStream(key, "g2");
        for (int i = 0; i < 5; i++) {
            MsgDemo msgDemo = new MsgDemo();
            msgDemo.setId(String.valueOf(i));
            redisStreamUtil.xadd(key, msgDemo, MsgDemo.class);
        }

        for (int i = 0; i < 5; i++) {
            System.out.println(redisStreamUtil.xreadObjectGroup(key, "g1", "c1", MsgDemo.class));
            System.out.println(redisStreamUtil.xreadObjectGroup(key, "g2", "c1", MsgDemo.class));
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;


}
