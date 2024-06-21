package com.ai.netty.websockerdemo.manager;

import cn.hutool.json.JSONUtil;
import com.ai.common.core.utils.StringUtils;
import com.ai.netty.buffer.Channel;
import com.ai.netty.msg.MsgImpl;
import com.ai.netty.msg.Task;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionManager {
    public static final  AttributeKey<Task> env_instance_database = AttributeKey.newInstance("env_instance_database");

    // key: environment_datasetId LRU算法
    public static final ConcurrentHashMap<String, NioSocketChannel> SESSION_CHANNEL = new ConcurrentHashMap<>(1 << 4);

    /**
     * 增加连接
     */
    @SneakyThrows
    public static void add(String channelKey, NioSocketChannel channel) {
//        final Task task = new Task();

        if (StringUtils.isBlank(channelKey)) {
            throw new IllegalAccessException("非法task");
        }

//        String[] s = channelKey.split("_");
//        String env = s[0];
//        String datasetId = s[1];
//        Integer syncType = getSyncType(env);
//        task.setTaskType(syncType);
//        task.setSourceDatasetId(Long.parseLong(datasetId));
//
//        channel.attr(env_instance_database).set(task);
        SESSION_CHANNEL.put(channelKey, channel);
    }

    /**
     * 获取连接
     */
    public NioSocketChannel get(String channelKey) {
        return SESSION_CHANNEL.get(channelKey);
    }

    /**
     * 删除连接
     */
    public static void remove(String channelKey) {
        NioSocketChannel channel = SESSION_CHANNEL.remove(channelKey);
        channel.attr(env_instance_database).set(null);
    }
    static int num = 0;

    public static <T> void sendToOne(String channelKey, MsgImpl protocol, T msg) throws InterruptedException {
        NioSocketChannel nioSocketChannel = SESSION_CHANNEL.get(channelKey);
        if (Objects.isNull(nioSocketChannel) || !nioSocketChannel.isActive()) {
            log.error("消息发送失败 ===========>}", nioSocketChannel);
            return;
        }

        protocol.setVersion(++num);
        if (nioSocketChannel.isActive() && nioSocketChannel.isWritable()) {
            TextWebSocketFrame frame = new TextWebSocketFrame(JSONUtil.toJsonStr(protocol));
            nioSocketChannel.writeAndFlush(frame).sync().addListener(future -> {
                log.info(future + "asdasdasdasdasd");
                        if (future.isSuccess()) {
                            log.info("===============>成功发送消息啦");
                        }
                    });
            // TODO 待会再发送
            // TODO 多次下推之后仍然没有成功的话，就移除channel连接并且关闭，并且清除ack队列的消息
//            log.error("message dropped");
        }
    }

    public static void sendAll() {

    }

    private static Integer getSyncType(String type) {
        if (type.equals("stg")) {
            return 1;
        } else if (type.equals("test")) {
            return 2;
        } else if (type.equals("dev")) {
            return 3;
        }
        return -1;

    }
}
