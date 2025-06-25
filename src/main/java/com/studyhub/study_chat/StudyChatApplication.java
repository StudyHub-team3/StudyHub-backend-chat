package com.studyhub.study_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StudyChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyChatApplication.class, args);
    }

}
