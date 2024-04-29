package com.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
public class MainApplicaion {
    public static void main(String[] args) {
        new SpringApplication().run(MainApplicaion.class);
    }
}
