package com.ai.netty.server.handler;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 因为处理url  的时候其实还是http协议，所以是使用FullHttpRequest消息
 * 而且因为WebSocketServerProtocolHandler为全路径匹配，也就是/websocket，当出现其他uri参数是不会匹配到，所以需要把其他参数放到context上下文中
 * checkStartWith全路径匹配，可以在构造器传入false取消全路径匹配
 */
@Component
@Slf4j
//@ChannelHandler.Sharable
public class WsParamsHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("与客户端建立连接，通道开启！");


        //添加到channelGroup通道组
//        MyChannelHandlerPool.channelGroup.add(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        log.info("NettyWebSocketParamHandler.channelRead0 --> : 格式化URL... {}", uri);
        Map<CharSequence, CharSequence> queryMap = UrlBuilder.ofHttp(uri).getQuery().getQueryMap();
        //将参数放入通道中传递下去
        AttributeKey<Integer> typeAttre = AttributeKey.valueOf("type");
        AttributeKey<String> envAttr = AttributeKey.valueOf("env");
        AttributeKey<String> cidAttr = AttributeKey.valueOf("cid");
        AttributeKey<String> ccidAttr = AttributeKey.valueOf("ccid");
        String cid = null;
        String env = null;
        Integer type = null;
        String ccid = null;
        if (queryMap.isEmpty()) {
            cid = "default";
            type = -1;
        } else {
            type = Integer.parseInt(queryMap.get("type").toString());
            env = queryMap.get("env").toString();
            cid = queryMap.get("cid").toString();
            if (queryMap.get("ccid") != null) {
                ccid = queryMap.get("ccid").toString();
                ctx.channel().attr(ccidAttr).setIfAbsent(ccid);
                log.info("+++++++++>{}", ccid);
            }

        }
        ctx.channel().attr(cidAttr).setIfAbsent(cid);
        ctx.channel().attr(typeAttre).setIfAbsent(type);
        ctx.channel().attr(envAttr).setIfAbsent(env);

        request.setUri(URLUtil.getPath(uri));

        ctx.fireChannelRead(request.retain());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("NettyWebSocketParamHandler.exceptionCaught --> cause: ", cause);
        ctx.close();
    }



}
