package com.ganghuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CoderCircleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoderCircleApplication.class, args);
    }

}
