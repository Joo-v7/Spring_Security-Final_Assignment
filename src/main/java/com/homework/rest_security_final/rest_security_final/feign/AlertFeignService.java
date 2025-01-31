package com.homework.rest_security_final.rest_security_final.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlertFeignService {

    private final AlertFeignClient alertFeignClient;

    @Autowired
    public AlertFeignService(AlertFeignClient alertFeignClient) {
        this.alertFeignClient = alertFeignClient;
    }

    public void alert(String memberId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("botName", "알림이");
        requestBody.put("text", memberId + "님 로그인 5회 실패, 로그인이 차단됩니다.");

        ResponseEntity<String> response = alertFeignClient.alert(requestBody);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("로그인 차단 메세지 전송 성공");
        } else {
            System.out.println("로그인 차단 메세지 전송 실패: " + response.getStatusCode());
        }
    }

}
