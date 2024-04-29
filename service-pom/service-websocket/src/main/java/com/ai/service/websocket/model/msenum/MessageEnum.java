package com.ai.service.websocket.model.msenum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageEnum {
    VECTOR(1 ,"向量"),
    STRING(2, "字符串");
    private Integer type;
    private String desc;

}
