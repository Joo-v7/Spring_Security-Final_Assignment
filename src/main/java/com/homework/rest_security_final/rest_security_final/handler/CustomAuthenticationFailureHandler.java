package com.homework.rest_security_final.rest_security_final.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.rest_security_final.rest_security_final.exception.MemberNotFoundException;
import com.homework.rest_security_final.rest_security_final.feign.AlertFeignService;
import com.homework.rest_security_final.rest_security_final.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final AlertFeignService alertFeignService;

    private final RedisTemplate<String, Object> redisTemplate;

    private String LOGIN_ATTEMPT_COUNT = "Login_Attempt_Count:";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String memberId = request.getParameter("id"); // request 에서 "id"(loginForm의 name이 id인 값) 가져오기

        // redis에 없는 ID 입력시 MemberDetailsService에서 이 오류 던짐.
        if(exception.getCause() instanceof IllegalArgumentException) {
            response.sendRedirect("/loginForm");
            return;
        }

//         UserDetailService의 구현체인 MemberDetailsService에서 확인해서 에러 던진거 여기서 받음.
        if(exception.getCause() instanceof AccessDeniedException) {
            log.info("accessdeny임");
                alertFeignService.alert(memberId); // 알람 보냄.
                response.sendRedirect("/403");
                return;
            }

        if(!redisTemplate.opsForHash().hasKey(LOGIN_ATTEMPT_COUNT, memberId)) { // id가 hash에 없으면
            Integer count = (Integer) 1;
            redisTemplate.opsForHash().put(LOGIN_ATTEMPT_COUNT,memberId, count);
            log.info("{}: 로그인 실패 {}회", memberId, count);
            response.sendRedirect("/loginForm");
            return;
        }

        Long count = redisTemplate.opsForHash().increment(LOGIN_ATTEMPT_COUNT, memberId, 1);

        if(count < 5) { // 5회 이하면
            log.info("{}: 로그인 실패 {}회", memberId, count);
            response.sendRedirect("/loginForm");
            return;
        }

        // 차단
        alertFeignService.alert(memberId); // 알람 보냄.
        response.sendRedirect("/403");

    }
}
