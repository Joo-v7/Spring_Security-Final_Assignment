package com.homework.rest_security_final.rest_security_final.service;

import com.homework.rest_security_final.rest_security_final.model.OAuth2UserImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes(); // 유저 정보(attributes) 가져옴

        OAuth2UserImpl oAuth2User = new OAuth2UserImpl(
                String.valueOf(oAuth2UserAttributes.get("sub")),
                String.valueOf(oAuth2UserAttributes.get("name")),
                String.valueOf(oAuth2UserAttributes.get("email"))
        );

        return oAuth2User;
    }

}

