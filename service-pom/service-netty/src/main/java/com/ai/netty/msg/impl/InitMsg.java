package com.ai.netty.msg.impl;

import lombok.Data;

import java.util.List;

@Data
public class InitMsg extends BaseMsg{
    private Long datasetId;
    private String partitionName;
    private List<String> partitions;
    // 总数据量
    private Integer count;
}

