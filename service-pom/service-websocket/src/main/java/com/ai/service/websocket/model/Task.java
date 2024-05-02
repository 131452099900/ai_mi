package com.ai.service.websocket.model;

import lombok.Data;

@Data
public class Task {
    Integer status;
    Integer count;
    Integer nowCount;
    String taskId;
}
