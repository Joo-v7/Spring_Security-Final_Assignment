package com.homework.rest_security_final.rest_security_final.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2UserImpl implements OAuth2User {

    private Map<String, Object> attributes = new HashMap<>();

    private String sub;
    private String name;
    private String email;
    private Role role;

    public OAuth2UserImpl() {}

    public OAuth2UserImpl(String sub, String name, String email) {
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.role = Role.GOOGLE;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_"+this.role;
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

    @Override
    public Map<String, Object> getAttributes() {
        this.attributes.put("sub", this.sub);
        this.attributes.put("name", this.name);
        this.attributes.put("email", this.email);
        this.attributes.put("role", this.role);

        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    public Role getRole() {
        return this.role;
    }

}
