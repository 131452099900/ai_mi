package com.ai.service.websocket.handle;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MsgHandlerFactory {
    static Map<Integer, AbsMsgHandler> map = new HashMap<>();
     static  {
        map.put(1, new StartHandler());
         map.put(2, new PongHandler());
         map.put(3, new PingHandler());
         map.put(4, new PrePocessHandler());
         map.put(5, new DataHandler());
         map.put(6, new EndHandler());
     }

    public static AbsMsgHandler getHandler(Integer type) {
         return map.get(type);
    }
}
