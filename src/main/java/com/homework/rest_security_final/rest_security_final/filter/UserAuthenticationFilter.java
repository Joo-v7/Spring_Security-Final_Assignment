package com.homework.rest_security_final.rest_security_final.filter;

import com.homework.rest_security_final.rest_security_final.model.Member;
import com.homework.rest_security_final.rest_security_final.model.MemberDetails;
import com.homework.rest_security_final.rest_security_final.model.OAuth2UserImpl;
import com.homework.rest_security_final.rest_security_final.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberService memberService;
    private String SESSION_HASH_NAME = "Session:";
    private String GOOGLE_HASH_NAME = "Google:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = null;

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie c : cookies) {
                if(c.getName().equals("SESSION")) {
                    sessionId = c.getValue();
                }
            }
        }

        if(sessionId != null) {
            Object o = redisTemplate.opsForHash().get(SESSION_HASH_NAME, sessionId);
            String memberId = (String) o; // google 로그인은 name 가져옴, 일반 로그인은 login ID(member ID) 가져옴

            if(!(Objects.isNull(memberId) || memberId.isEmpty())) {
                boolean isGoogle = redisTemplate.opsForHash().hasKey(GOOGLE_HASH_NAME, sessionId);

                if(memberService.existsById(memberId)) { // memberId가 redis에 존재하면.
                    Member member = memberService.getMemberById(memberId);

                    /*
                    * MemberDetails 객체를 생성하여 인증 정보를 구성함.
                    * PreAuthenticatedAuthenticationToken을 사용해 인증 객체를 생성하고,
                    * 이를 SecurityContextHolder에 설정하여 현재 요청에 대해 인증된 상태를 유지합니다.
                    * */
                    MemberDetails memberDetails = new MemberDetails(member);
                    Authentication authentication = new PreAuthenticatedAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
                    authentication.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
                // TODO: 구글로 로그인했을때 서버 재시작해도 유지되게 하기.
                else if(isGoogle) { // oAuth로 로그인하면 google redis 에서 name 가져옴.
                    OAuth2UserImpl oAuth2User = (OAuth2UserImpl) redisTemplate.opsForHash().get(GOOGLE_HASH_NAME, sessionId);
                    Authentication authentication = new PreAuthenticatedAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
                    authentication.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
