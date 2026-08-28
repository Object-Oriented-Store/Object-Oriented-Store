package com.ohgiraffers.store.member.service;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.repository.MemberDAO;
import com.ohgiraffers.store.member.repository.MembershipGradeDAO;

public class MemberService {

    private final MemberDAO memberDAO = new MemberDAO();
    private final MembershipGradeDAO memberGradeDAO = new MembershipGradeDAO();

    public boolean joinMember(MemberDTO member) {

        if (member == null) {
            return false;
        }

        if (member.getLoginId() == null || member.getLoginId().isBlank() || member.getLoginId().length() > 25) {
            System.out.println("아이디를 입력해주세요.");
            return false;
        }

        if (member.getPassword() == null || member.getPassword().isBlank()) {
            System.out.println("비밀번호를 입력해주세요.");
            return false;
        }

        if (member.getNickname() == null || member.getNickname().isBlank() || member.getNickname().length() > 25) {
            System.out.println("닉네임을 입력해주세요.");
            return false;
        }

        String phone = String.valueOf(member.getPhone());
        if (phone == null || phone.isBlank() || phone.length() != 8) {
            System.out.println("휴대폰번호를 입력해주세요.");
            return false;
        }
        // 아이디 중복 검증으로  false일 시 회원가입 진행
        if (memberDAO.isLoginIdDuplicate(member.getLoginId())) {
            System.out.println("이미 사용중인 아이디입니다.");
            return false;
        }

        int result = memberDAO.insertMember(member);
        return result > 0;
    }

    // 로그인 입력값 검증
    public MemberDTO loginMember(MemberDTO member) {

        if (member == null) {
            return null;
        }

        if (member.getLoginId() == null || member.getLoginId().isBlank()) {
            return null;
        }

        if (member.getPassword() == null || member.getPassword().isBlank()) {
            return null;
        }
        return memberDAO.login(member.getLoginId(), member.getPassword()
        );
    }

    // 로그인한 회원의 등급명 조회
    public String findGradeName(MemberDTO loginMember) {

        if (loginMember == null) {
            return null;
        }
        return memberGradeDAO.selectGradeName(loginMember.getGradeCode()
        );
    }

    // 로그인한 회원의 정보 조회 (로그인이 안되어 있거나 가입된 계정이 아닐 시 null)
    public MemberDTO selectMember(MemberDTO loginMember) {

        if (loginMember == null) {
            return null;
        }
        if (loginMember.getMemberCode() <= 0) {
            return null;
        }
        return memberDAO.selectMember(loginMember);
    }

    // 정보 수정
    public boolean modifyMember(MemberDTO member) {

        if (member == null) {
            return false;
        }

        if (member.getMemberCode() <= 0) {
            return false;
        }

        if (member.getPassword() == null
                || member.getPassword().isBlank()) {

            System.out.println("비밀번호를 입력해주세요.");
            return false;
        }

        if (member.getNickname() == null
                || member.getNickname().isBlank()
                || member.getNickname().length() > 25) {

            System.out.println("닉네임을 올바르게 입력해주세요.");
            return false;
        }

        String phone = String.valueOf(member.getPhone());

        if (!phone.matches("[0-9]{8}")) {
            System.out.println(
                    "휴대폰 번호는 숫자 8자리로 입력해주세요."
            );
            return false;
        }

        int result = memberDAO.modifyMember(member);

        return result > 0;
    }
}