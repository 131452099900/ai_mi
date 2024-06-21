package com.ai.controller;

import cn.hutool.json.JSONUtil;
import com.ai.netty.websockerdemo.cache.TaskCache;
import com.ai.netty.websockerdemo.manager.WebsocketClientManager;
import com.ai.netty.websockerdemo.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @PostMapping("/syncConnect")
    public String A(@RequestBody  Map<Object, Object> map) throws Exception {
        final WebsocketClientManager build = new WebsocketClientManager.Builder().build(false);
        final boolean localhost = build.connect("localhost", 6666, "env=prod&type=2&cid=prod_111&ccid="+map.get("sourceId"));
        if(localhost) {
//            log.info("2.1  {}", );
        }
        // 2.1 初始化任务
        TaskState task = new TaskState();
        task.setCid("prod_111");
        task.setState(2);
        TaskCache.put("prod_111", task);
        return JSONUtil.toJsonStr(map);
    }


}
