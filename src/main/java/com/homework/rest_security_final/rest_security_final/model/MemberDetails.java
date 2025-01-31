package com.homework.rest_security_final.rest_security_final.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;

public class MemberDetails implements UserDetails {

    private String id;
    private String name;
    private String password;
    private Integer age;
    private Role role;

    public MemberDetails(Member member) {
        this.id= member.getId();
        this.name= member.getName();
        this.password= member.getPassword();
        this.age= member.getAge();
        this.role=member.getRole();
    }

    public MemberDetails(Member member, PasswordEncoder passwordEncoder) {
        this.id= member.getId();
        this.name= member.getName();
        this.password= passwordEncoder.encode(member.getPassword());
        this.age= member.getAge();
        this.role=member.getRole();
    }

    //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_"+this.role;
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getId() {
        return this.id;
    }

}
