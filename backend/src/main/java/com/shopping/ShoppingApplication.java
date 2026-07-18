package com.shopping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.shopping.mapper")
@EnableScheduling
public class ShoppingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }
}
