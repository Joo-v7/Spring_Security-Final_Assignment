package com.homework.rest_security_final.rest_security_final.service;

import com.homework.rest_security_final.rest_security_final.model.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

// 로그인 요청이 발생하면, Spring Security는 UserDetailsService의 loadUserByUsername 메소드 호출해서 아이디로 조회해서, 이걸로 인증된 사용자 정보(UserDetails 객체)를 로드함.
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final MemberService memberService;

    private final RedisTemplate<String, Object> redisTemplate;

    private String LOGIN_ATTEMPT_COUNT = "Login_Attempt_Count:";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(!memberService.existsById(username)) {
            throw new IllegalArgumentException("존재하지 않는 ID 입니다.");
        }

        Integer count = (Integer) redisTemplate.opsForHash().get(LOGIN_ATTEMPT_COUNT ,username); // username이 로그인 할 떄 사용한 ID임.

        if(Objects.nonNull(count)) {
            if(count > 4) {
                log.info("accessdeny던짐");
                throw new AccessDeniedException("로그인이 차단된 사용자 입니다.");
            }
        }

        return new MemberDetails(memberService.getMemberById(username), passwordEncoder);
    }
}
