package com.homework.rest_security_final.rest_security_final.handler;

import com.homework.rest_security_final.rest_security_final.model.MemberDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String SESSION_HASH_NAME = "Session:";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        /* 로그인하면 쿠키 세션 생성하고, 레디스에 세션 생성함. */

        // session.getId가 login id임
        HttpSession session = request.getSession(false);

        Cookie cookie = new Cookie("SESSION", session.getId());
        cookie.setHttpOnly(true); // 보안 설정
        cookie.setMaxAge(60*60); // 쿠키 유효 시간(1시간)
        cookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
        response.addCookie(cookie);

        // authentication 객체의 getPrincipal()이 return 하는 값은 UserDetails interface를 implements한 클래스의 instance임.
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        redisTemplate.opsForHash().put(SESSION_HASH_NAME, session.getId(), memberDetails.getId()); // Redis에 Session:{sessionID(loginId), memberDetails ID} 저장.
        redisTemplate.expire(SESSION_HASH_NAME, 60, TimeUnit.MINUTES); // Redis에 세션 만료 시간 설정.

        response.sendRedirect("/");
    }

}
