package com.ohgiraffers.store.member.controller;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.service.MemberService;


public class MemberController {

    private final MemberService memberService = new MemberService();

    public MemberDTO joinMember(String loginId, String password, String nickname, int phone) {
        MemberDTO member = new MemberDTO(loginId, password, nickname, phone);

        return memberService.joinMember(member);
    }

    public MemberDTO selectMember(MemberDTO loginMember) {
        return memberService.selectMember(loginMember);
    }
    public boolean modifyMember(MemberDTO member) {
        return memberService.modifyMember(member);
    }

    public String selectGradeName(MemberDTO member){
        return memberService.selectGradeName(member);
    }

    public boolean plusTotalAmount(int memberCode, int finalAmount){
        return memberService.plusTotalAmount(memberCode,finalAmount);
    }

    public boolean minusTotalAmount(int memberCode, int previousTotalAmount){
        return memberService.minusTotalAmount(memberCode,previousTotalAmount);
    }
}


