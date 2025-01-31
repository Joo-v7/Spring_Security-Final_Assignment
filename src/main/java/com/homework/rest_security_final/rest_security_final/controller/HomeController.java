package com.homework.rest_security_final.rest_security_final.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @GetMapping("/")
    public ModelAndView home(Authentication authentication) {
        ModelAndView mav = new ModelAndView("home");
        String role = parseAuthority(authentication.getAuthorities());

        mav.addObject("userName", authentication.getName());
        mav.addObject("role", role);

        return mav;
    }

    private String parseAuthority(Collection<? extends GrantedAuthority> authority) {
        String autho = authority.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        if(autho.contains("MEMBER")) {
            return "일반 멤버";
        }

        if(autho.contains("ADMIN")) {
            return "관리자";
        }

        if(autho.contains("GOOGLE")) {
            return "구글 멤버";
        }

        throw new IllegalArgumentException("멤버의 권한 정보가 없습니다.");
    }

}

