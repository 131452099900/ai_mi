package com.ai.netty.websockerdemo.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AuthServiceImpl {

    public boolean auth(Integer type, String token) {
        // 根据类型向对应服务器发送
        return true;
    }

    public CompletableFuture<Boolean> authSync(Integer type,String cid) {
        return CompletableFuture.supplyAsync(() -> {
            // 调用Feign客户端执行同步HTTP请求
            return true;
        });
    }
}
