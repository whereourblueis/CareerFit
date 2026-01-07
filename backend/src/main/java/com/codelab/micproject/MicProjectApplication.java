package com.codelab.micproject;

import com.codelab.micproject.resume.config.GptProperties; // <-- import 구문 추가
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties; // <-- import 구문 추가

@EnableConfigurationProperties(GptProperties.class) // <-- 이 어노테이션을 추가해주세요.
@SpringBootApplication
public class MicProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicProjectApplication.class, args);
    }
}