package com.ohgiraffers.store.member.controller;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.service.MemberService;


public class MemberController {

    // View에서 받은 요청을 DTO 또는 기본값으로 정리해 Service에 전달
    private final MemberService memberService = new MemberService();

    public MemberDTO joinMember(String loginId, String password, String nickname, int phone) {
        // 화면에서 받은 가입 입력값을 하나의 회원 DTO로 묶음
        MemberDTO member = new MemberDTO(loginId, password, nickname, phone);

        return memberService.joinMember(member);
    }

    public boolean isLoginIdAvailable(String loginId) {
        return memberService.isLoginIdAvailable(loginId);
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

    public boolean withdrawMember(MemberDTO loggedInMember) {
        // 로그인 회원 객체를 전달해 본인 계정을 탈퇴 처리
        return memberService.withdrawMember(loggedInMember);
    }
}
