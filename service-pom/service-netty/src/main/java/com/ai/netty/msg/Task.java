package com.ai.netty.msg;

import lombok.Data;

@Data
public class Task {
    private Integer taskType;
    private Long sourceDatasetId;
    private Long targetDatasetId;
    private Long nowCount;
    private Long totolCount;
}
