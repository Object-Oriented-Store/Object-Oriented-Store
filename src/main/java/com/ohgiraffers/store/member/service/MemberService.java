package com.ohgiraffers.store.member.service;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.repository.MemberDAO;
import com.ohgiraffers.store.member.repository.MembershipGradeDAO;

public class MemberService {

    private final MemberDAO memberDAO = new MemberDAO();
    private final MembershipGradeDAO memberGradeDAO = new MembershipGradeDAO();
    private final MemberDTO memberDTO = new MemberDTO();

    // 회원 가입 검증
    public MemberDTO joinMember(MemberDTO member) {

        if (member == null) {
            return null;
        }
        if (member.getLoginId() == null || member.getLoginId().isBlank() || member.getLoginId().length() > 25) {
            System.out.println("아이디를 입력해주세요.");
            return null;
        }
        if (member.getPassword() == null || member.getPassword().isBlank()) {
            System.out.println("비밀번호를 입력해주세요.");
            return null;
        }
        if (member.getNickname() == null || member.getNickname().isBlank() || member.getNickname().length() > 25) {
            System.out.println("닉네임을 입력해주세요.");
            return null;
        }
        if (member.getPhone() < 0 || member.getPhone() > 99_999_999) {
            System.out.println(
                    "휴대폰 번호는 숫자 8자리로 입력해주세요.");
            return null;
        }

        // 아이디 중복 검증으로  false일 시 회원가입 진행
        if (memberDAO.isLoginIdDuplicate(member.getLoginId())) {
            System.out.println("이미 사용중인 아이디입니다.");
            return null;
        }

        int result = memberDAO.insertMember(member);

        if (result <= 0){
            return null;
        }
        return memberDAO.selectMemberByLoginId(member.getLoginId());
    }

    public MemberDTO selectMember(MemberDTO loggedInMember){
        if(loggedInMember == null){
            return null;
        }
        if (loggedInMember.getMemberCode() <= 0){
            return null;
        }
        return memberDAO.selectMember(loggedInMember);
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

        if (member.getPhone() < 0 || member.getPhone() > 99_999_999) {
            System.out.println(
                    "휴대폰 번호는 숫자 8자리로 입력해주세요.");
            return false;
        }

        int result = memberDAO.modifyMember(member);

        return result > 0;
    }

    // 내정보 등급명 표기
    public String selectGradeName(MemberDTO member) {

        if (member == null || member.getGradeCode() <= 0) {
            return null;
        }

        return memberGradeDAO.selectGradeName(member.getGradeCode());
    }

    // 누적 구매 금액 추가
    public boolean plusTotalAmount(int memberCode, int finalAmount){

        if (memberCode <= 0){
            return false;
        }
        if (finalAmount <= 0){
            return false;
        }
        int amountResult = memberDAO.plusTotalAmount(memberCode, finalAmount);

        if (amountResult <= 0){
            return false;
        }

        int gradeResult = memberGradeDAO.updateMembershipGrade(memberCode);

        return gradeResult > 0;
    }

    // 누적 구매 금액 차감
    public boolean minusTotalAmount(int memberCode, int previousAmount){

        if (memberCode <= 0){
            return false;
        }
        if (previousAmount <= 0){
            return false;
        }
        int amountResult = memberDAO.minusTotalAmount(memberCode, previousAmount);

        if (amountResult <= 0){
            return false;
        }

        int gradeResult = memberGradeDAO.updateMembershipGrade(memberCode);

        return gradeResult > 0;
    }

    public boolean updateMembershipGrade(int memberCode){

        if (memberCode <= 0){
            return false;
        }

        int result = memberGradeDAO.updateMembershipGrade(memberCode);

        return result > 0;
    }
}