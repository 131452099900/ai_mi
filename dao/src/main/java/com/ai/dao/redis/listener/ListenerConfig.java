package com.ai.dao.redis.listener;

import com.ai.dao.redis.utils.RedisStreamUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.*;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ListenerConfig {

    @Value("server.port")
    private String port;
    @Autowired
    private ListenMsgStream listenMsgStream;
    @Autowired
    private ListenMsgStream2 listenMsgStream2;
    @Autowired
    private RedisStreamUtil redisStreamUtil;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private ConsumerStreamReadRequest<String> buildRequest(String key, String group, String consumerName) {

        // 创建stream和group信息
        redisStreamUtil.initStream(key, group);

        // 指定消费最新消息
        StreamOffset<String> lastMsgOffset = StreamOffset.create(key, ReadOffset.lastConsumed());
        // 创建消费者
        Consumer consumer = Consumer.from(group, consumerName);

        return StreamReadRequest
                .builder(lastMsgOffset)
                .errorHandler((error) -> {})
                .cancelOnError(e -> false)
                .consumer(consumer)
                .autoAcknowledge(false) //不自动ACK确认
                .build();
    }

    private StreamMessageListenerContainerOptions<String, ObjectRecord<String, MsgDemo>> buildOption () {
        //创建容器
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, MsgDemo>> build = StreamMessageListenerContainerOptions
                .builder()
                // ObjectRecord 时，将 对象的 filed 和 value 转换成一个 Map 比如：将Book对象转换成map
                .objectMapper(new ObjectHashMapper())
                .pollTimeout(Duration.ofSeconds(5))
                .errorHandler(new MsgErrorHandler())
                .targetType(MsgDemo.class)
                .executor(threadPoolTaskExecutor)   // 线程执行器
                .pollTimeout(Duration.ofSeconds(1)) // 等待消息阻塞时长
                .batchSize(10)
                .build();
        return build;
    }


    public static final String WEBSOCKET_QUEUE = "WEBSOCKET_QUEUE";
    public static final String WEBSOCKET_QUEUE_GROUP = "WEBSOCKET_QUEUE_GROUP";

    public static final String QUEUE1 = "qwjsadswasdasdqwj";
    public static final String QUEUE1_GROUP1 = "Q1_GROUP1";
    public static final String QUEUE1_GROUP1_CONSUMER1 = "Q1_GROUP1_CONSUMER1";


    public static final String QUEUE1_GROUP2 = "Q_GROUP2";
    public static final String QUEUE1_GROUP2_CONSUMER1 = "Q1_GROUP2_CONSUMER1";

//    @Bean
//    public List<Subscription> subscriptionMsgListener(RedisConnectionFactory factory) {
//        StreamMessageListenerContainerOptions<String, ObjectRecord<String, MsgDemo>> options = buildOption();
//
//        StreamMessageListenerContainer<String, ObjectRecord<String, MsgDemo>> listenerContainer
//                = StreamMessageListenerContainer.create(factory, options);
//
////        listenerContainer.receive(StreamOffset.fromStart(QUEUE1), listenMsgStream);
////        listenerContainer.receive(StreamOffset.fromStart(QUEUE1), listenMsgStream2);
//
//
//        ConsumerStreamReadRequest<String> request = buildRequest(WEBSOCKET_QUEUE, WEBSOCKET_QUEUE_GROUP + port, QUEUE1_GROUP1_CONSUMER1);
////        ConsumerStreamReadRequest<String> request1 = buildRequest(QUEUE1, QUEUE1_GROUP2, QUEUE1_GROUP2_CONSUMER1);
//
//        //将监听类绑定到相应的stream流上
////        Subscription subscription2 = listenerContainer.register(request1, listenMsgStream2);
//
//        //将监听类绑定到相应的stream流上
//        Subscription subscription = listenerContainer.register(request, listenMsgStream);
//
//        //启动监听
//        listenerContainer.start();
//
//        List<Subscription> subscriptions = new ArrayList<>();
//        subscriptions.add(subscription);
////        subscriptions.add(subscription2);
//
//
//        return subscriptions;
//    }

//    @Bean
//    public StreamMessageListenerContainer<String, ObjectRecord<String, MsgDemo>> subscriptionMsgListener(RedisConnectionFactory factory) {
//        StreamMessageListenerContainerOptions<String, ObjectRecord<String, MsgDemo>> options = buildOption();
//
//        StreamMessageListenerContainer<String, ObjectRecord<String, MsgDemo>> listenerContainer
//                = StreamMessageListenerContainer.create(factory, options);
//
//        listenerContainer.receive(StreamOffset.fromStart(QUEUE1), listenMsgStream);
//        listenerContainer.receive(StreamOffset.fromStart(QUEUE1), listenMsgStream2);
//
////        ConsumerStreamReadRequest<String> request = buildRequest(QUEUE1, QUEUE1_GROUP1, QUEUE1_GROUP1_CONSUMER1);
////        ConsumerStreamReadRequest<String> request1 = buildRequest(QUEUE1, QUEUE1_GROUP2, QUEUE1_GROUP2_CONSUMER1);
//
////        //将监听类绑定到相应的stream流上
////        Subscription subscription2 = listenerContainer.register(request1, listenMsgStream2);
////
////        //将监听类绑定到相应的stream流上
////        Subscription subscription = listenerContainer.register(request, listenMsgStream);
////
////        //启动监听
////        listenerContainer.start();
////
////        List<Subscription> subscriptions = new ArrayList<>();
////        subscriptions.add(subscription);
////        subscriptions.add(subscription2);
//
//
//        return listenerContainer;
//    }
////


    @Value("${server.name}")
    private String name;
    @Bean
    public Subscription subscriptionMsgListener(RedisConnectionFactory factory) {
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, MsgDemo>> options = buildOption();

        StreamMessageListenerContainer<String, ObjectRecord<String, MsgDemo>> listenerContainer
                = StreamMessageListenerContainer.create(factory, options);

        System.out.println(name);
        ConsumerStreamReadRequest<String> request = buildRequest(WEBSOCKET_QUEUE, WEBSOCKET_QUEUE_GROUP + name, QUEUE1_GROUP1_CONSUMER1);

        //将监听类绑定到相应的stream流上
        Subscription subscription = listenerContainer.register(request, listenMsgStream);

        //启动监听
        listenerContainer.start();



        return subscription;
    }
}
