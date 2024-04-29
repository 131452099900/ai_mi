package com.ai.service.websocket.interceptor;

import com.ai.common.core.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;
import java.util.Objects;

// 三次握手监听 校验权限
@Component
@Slf4j
public class MyHandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {
    /**
     * 握手前
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @SneakyThrows
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String[] split = request.getURI().getQuery().trim().split("=");
        String token = split[1];
        // 校验时效性和有效性
        if (StringUtils.isEmpty(token)) {
            log.warn("token为空");
            return false;
        }
        attributes.put("token", token);
        return true;
    }

    /**
     * 握手后
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("=========>    握手后");

    }
}
