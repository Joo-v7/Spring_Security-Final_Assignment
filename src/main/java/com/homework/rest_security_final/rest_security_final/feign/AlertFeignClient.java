package com.homework.rest_security_final.rest_security_final.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name="AlertClient", url="https://hook.dooray.com")
public interface AlertFeignClient {
    @PostMapping("/services/3204376758577275363/3939383629139110347/6GoYNd1QT2SBd1DXke0Yrg")
    ResponseEntity<String> alert(@RequestBody Map<String, Object> requestBody);
}
