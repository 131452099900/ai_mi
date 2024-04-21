package com.ai.dao.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class MsgErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("--------> msg消费异常");
    }
}
