package com.ai.netty.websockerdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestEnum {

    REQUEST(1, "请求连接"),
    REQUEST_RESPONSE(2, "接收者请求连接");
    Integer id;
    String desc;
}
