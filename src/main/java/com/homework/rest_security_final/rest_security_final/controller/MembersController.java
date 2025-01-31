package com.homework.rest_security_final.rest_security_final.controller;

import com.homework.rest_security_final.rest_security_final.model.Member;
import com.homework.rest_security_final.rest_security_final.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MembersController {

    private final MemberService memberService;

    @GetMapping("/members")
    public List<Member> getMembers(Pageable pageable) {
        return memberService.getAll(pageable);
    }

    @PostMapping("/members")
    public ResponseEntity postMember(@RequestBody Member member) {

        memberService.register(member.getId(), member.getName(),
                member.getPassword(), member.getAge(), member.getRole());
        return ResponseEntity.ok(200);
    }

    @GetMapping("/members/{memberId}")
    public Member getMember(@PathVariable("memberId") String memberId) {
        return memberService.getMemberById(memberId);
    }

}
