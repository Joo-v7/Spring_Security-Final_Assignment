package com.homework.rest_security_final.rest_security_final.service;

import com.homework.rest_security_final.rest_security_final.exception.MemberAlreadyExistsException;
import com.homework.rest_security_final.rest_security_final.exception.MemberNotFoundException;
import com.homework.rest_security_final.rest_security_final.model.Member;
import com.homework.rest_security_final.rest_security_final.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    private String MEMBER_HASH_NAME = "Member:";

    public void register(String id, String name, String password, Integer age, Role role) {
        if(Objects.isNull(id) || id.isEmpty() || Objects.isNull(name) || name.isEmpty() || Objects.isNull(password) ||
        password.isEmpty() || Objects.isNull(age) || Objects.isNull(role)) {
            throw new IllegalArgumentException("입력한 정보들 중 없거나 비어있는 정보가 있습니다.");
        }

        if(redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, id)) {
            throw new MemberAlreadyExistsException("중복된 ID 이므로 사용할 수 없습니다.");
        }

        Member member = new Member(id, name, passwordEncoder.encode(password), age, role);

        redisTemplate.opsForHash().put(MEMBER_HASH_NAME, id, member);
    }

    public Member getMemberById(String memberId) {
        if(Objects.isNull(memberId) || memberId.isEmpty()) {
            throw new IllegalArgumentException("입력한 ID가 비어있습니다.");
        }

        if(!redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, memberId)) {
            throw new MemberNotFoundException("존재하지 않는 ID 입니다.");
        }

        return (Member) redisTemplate.opsForHash().get(MEMBER_HASH_NAME, memberId);
    }

    public List<Member> getAll(Pageable pageable) {
        List<Object> members = redisTemplate.opsForHash().values(MEMBER_HASH_NAME);
        List<Member> memberList = new ArrayList<>();

        for(Object o : members) {
            memberList.add((Member) o);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start+pageable.getPageSize(), memberList.size());

        return memberList.subList(start, end);
    }

    public boolean existsById(String memberId) {
        if(Objects.isNull(memberId) || memberId.isEmpty()) {
            return false;
        }
        return redisTemplate.opsForHash().hasKey(MEMBER_HASH_NAME, memberId);
    }

}
