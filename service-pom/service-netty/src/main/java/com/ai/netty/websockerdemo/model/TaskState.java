package com.ai.netty.websockerdemo.model;

import lombok.Data;

import java.util.List;

@Data
public class TaskState {
    private String cid;
    private String ccid;
    // 0:失败 1:待连接    2:连接成功未开始发送数据  3:接收了init包进入数据发送阶段  4:完成    5 等待接收
    private Integer state;

    private Integer datasetId;
    private String destDatasetId;
    private List<String> documentName;
    private Integer totol;
    private Integer nowCount;
    private String datasetName;
    private String destDataName;
}
