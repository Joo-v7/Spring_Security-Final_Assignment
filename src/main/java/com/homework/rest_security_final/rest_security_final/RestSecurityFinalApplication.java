package com.homework.rest_security_final.rest_security_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RestSecurityFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestSecurityFinalApplication.class, args);
    }

}
