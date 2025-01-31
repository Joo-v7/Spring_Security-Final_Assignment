package com.homework.rest_security_final.rest_security_final.handler;

import com.homework.rest_security_final.rest_security_final.model.OAuth2UserImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String SESSION_HASH_NAME = "Session:";

    private String GOOGLE_HASH_NAME = "Google:";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        invalidateExistingSession(request, response);

        HttpSession session = request.getSession(true);
        Cookie cookie = new Cookie("SESSION", session.getId());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60*60);
        cookie.setPath("/");
        response.addCookie(cookie);

        OAuth2UserImpl oAuth2UserImpl = (OAuth2UserImpl) authentication.getPrincipal();
        // session redis에 넣음
        redisTemplate.opsForHash().put(SESSION_HASH_NAME, session.getId(), oAuth2UserImpl.getName());
        redisTemplate.expire(SESSION_HASH_NAME, 60, TimeUnit.MINUTES); // Redis에 세션 만료 시간 설정.

        // google redis에 {name, oAuth2User} 저장.
        redisTemplate.opsForHash().put(GOOGLE_HASH_NAME, session.getId(), oAuth2UserImpl);

        response.sendRedirect("/");

    }

    private void invalidateExistingSession(HttpServletRequest req, HttpServletResponse resp) {

        // 세션 무효화.
        HttpSession session = req.getSession(false);
        if(Objects.isNull(session)) {
            return;
        }
        session.invalidate();

        // 기존 쿠키 지우기.
        String existsSessionId = null;
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for(Cookie c : cookies) {
                if(c.getName().equals("SESSION")) {
                    existsSessionId = c.getValue();
                }
            }
        }
        Cookie cookie = new Cookie("SESSION", existsSessionId);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

}
