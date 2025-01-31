package com.homework.rest_security_final.rest_security_final.config;

import com.homework.rest_security_final.rest_security_final.filter.UserAuthenticationFilter;
import com.homework.rest_security_final.rest_security_final.handler.CustomAuthenticationFailureHandler;
import com.homework.rest_security_final.rest_security_final.handler.CustomAuthenticationSuccessHandler;
import com.homework.rest_security_final.rest_security_final.handler.CustomLogoutHandler;
import com.homework.rest_security_final.rest_security_final.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.homework.rest_security_final.rest_security_final.service.CustomOauth2UserService;
import com.homework.rest_security_final.rest_security_final.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberService memberService;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // csrf disable
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // login authentication
        httpSecurity.formLogin(formLogin -> formLogin
                .loginPage("/loginForm")
                .usernameParameter("id")
                .passwordParameter("password")
                .loginProcessingUrl("/loginForm/process") // 로그인 인증 요청을 처리하는 URL (POST 요청) -> UsernamePasswordAuthenticationFilter가 작동하여 로그인 인증 요청 처리함.
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customAuthenticationSuccessHandler)
        );

        // 구글 OAuth2 로그인
        httpSecurity.oauth2Login(oauth2 -> oauth2
                .loginPage("/loginForm")
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOauth2UserService))
                .successHandler(customOAuth2AuthenticationSuccessHandler)
        );

        // User Authentication
        httpSecurity.addFilterBefore(new UserAuthenticationFilter(redisTemplate, memberService), UsernamePasswordAuthenticationFilter.class);

        // Authorization
        httpSecurity.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.GET, "/members").permitAll() // 테스트용 멤버 조회 열음
                        .requestMatchers(HttpMethod.POST, "/members").permitAll() // 테스트용 멤버 생성 열음
                        .requestMatchers("/403").permitAll()
                        .requestMatchers("/loginForm").permitAll()
                        .requestMatchers("/members","/members/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER", "ROLE_GOOGLE") // /members 는 member controller에서 사용함.
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Spring Security는 역할(Role)을 확인할 때, hasRole("MEMBER")라고 호출하면 실제로는 ROLE_MEMBER를 찾음.
                        .requestMatchers("/member/**").hasRole("MEMBER")
                        .requestMatchers("/google/**").hasRole("GOOGLE")
                        .anyRequest().authenticated()
                );

        // security logout
        httpSecurity.logout(logout -> logout
                .logoutSuccessUrl("/loginForm")
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler((request, response, authentication) ->
                        response.sendRedirect("/loginForm"))
                .deleteCookies("JSESSIONID", "SESSION")
        );

        // 오류 발생시 "/403" 리다이렉트
        httpSecurity.exceptionHandling(e -> e.accessDeniedPage("/403"));

        return httpSecurity.build();
    }
}
