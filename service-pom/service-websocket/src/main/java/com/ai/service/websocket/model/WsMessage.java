package com.ai.service.websocket.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class WsMessage implements Serializable {
    private Integer type;
    private String data;
    private String origin;
}
