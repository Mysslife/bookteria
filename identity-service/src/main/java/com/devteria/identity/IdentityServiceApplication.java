package com.devteria.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // vì service Identity này gọi đến service Profile nên phải dùng Open Feign ở đây
// open feign là 1 http client, đi lấy dữ liệu ở nơi khác để cung cấp cho service, nên sẽ được đặt ở folder repository
public class IdentityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}


