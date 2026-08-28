package com.ohgiraffers.store.member.controller;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.service.MemberService;


public class MemberController {

    private final MemberService memberService = new MemberService();

    public boolean joinMember(String loginId, String password, String nickname, int phone) {
        MemberDTO member = new MemberDTO(loginId, password, nickname, phone);

        return memberService.joinMember(member);
    }

    // LoginId 및 password 받기
    public MemberDTO loginMember(String loginId, String password) {

        MemberDTO member = new MemberDTO(loginId, password);

        return memberService.loginMember(member);
    };
    public String findGradeName(MemberDTO loginMember) {
        return memberService.findGradeName(loginMember);
    }
}


