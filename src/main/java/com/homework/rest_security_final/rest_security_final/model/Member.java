package com.homework.rest_security_final.rest_security_final.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class Member {

    private String id;
    private String name;
    private String password;
    private Integer age;
    private Role role;

    public Member() {}

    @Override
    public boolean equals(Object o) {

        if(this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return member.getId().equals(id) &&
                member.getName().equals(name) &&
                member.getPassword().equals(password) &&
                member.getAge().equals(age) &&
                member.getRole().equals(role);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, age, role);
    }
}
