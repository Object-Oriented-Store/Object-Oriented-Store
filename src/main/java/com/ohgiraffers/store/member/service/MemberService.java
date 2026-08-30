package com.ohgiraffers.store.member.service;

import com.ohgiraffers.store.member.model.MemberDTO;
import com.ohgiraffers.store.member.repository.MemberDAO;
import com.ohgiraffers.store.member.repository.MembershipGradeDAO;

import java.sql.Connection;

public class MemberService {

    // Service는 입력값과 업무 규칙을 검증한 뒤 필요한 DAO를 호출
    private final MemberDAO memberDAO = new MemberDAO();
    private final MembershipGradeDAO memberGradeDAO = new MembershipGradeDAO();
    private final MemberDTO memberDTO = new MemberDTO();

    // 회원가입 전 아이디 사용 가능 여부 확인
    public boolean isLoginIdAvailable(String loginId) {

        if (loginId == null || loginId.isBlank() || loginId.length() > 25) {

            System.out.println("아이디를 올바르게 입력해주세요.");
            return false;
        }

        if (memberDAO.isLoginIdDuplicate(loginId)) {
            System.out.println("이미 사용 중인 아이디입니다.");
            return false;
        }

        return true;
    }

    // 회원 가입 검증
    public MemberDTO joinMember(MemberDTO member) {

        if (member == null) {
            return null;
        }
        if (member.getLoginId() == null || member.getLoginId().isBlank() || member.getLoginId().length() > 25) {
            System.out.println("아이디를 입력해주세요.");
            return null;
        }
        if (memberDAO.isLoginIdDuplicate(member.getLoginId())) {
            System.out.println("이미 사용중인 아이디입니다.");
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
        // 멤버십 가입 정보 저장 및 가입 결과 반환
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
    public boolean plusTotalAmount(Connection con, int memberCode, int finalAmount){

        if (memberCode <= 0){
            return false;
        }
        if (finalAmount <= 0){
            return false;
        }
        int amountResult = memberDAO.plusTotalAmount(con, memberCode, finalAmount);

        if (amountResult <= 0){
            return false;
        }

        // 누적 금액이 변경된 직후 현재 누적 금액을 기준으로 등급도 다시 계산
        int gradeResult = memberGradeDAO.updateMembershipGrade(con, memberCode);

        return gradeResult > 0;
    }

    // 누적 구매 금액 차감
    public boolean minusTotalAmount(Connection con, int memberCode, int previousAmount){

        if (memberCode <= 0){
            return false;
        }
        if (previousAmount <= 0){
            return false;
        }
        int amountResult = memberDAO.minusTotalAmount(con, memberCode, previousAmount);

        if (amountResult <= 0){
            return false;
        }

        // 결제 취소로 누적 금액이 줄면 등급이 내려갈 수도 있으므로 다시 계산
        int gradeResult = memberGradeDAO.updateMembershipGrade(con, memberCode);

        return gradeResult > 0;
    }

    // 멤버십 등급 업데이트
    public boolean updateMembershipGrade(Connection con, int memberCode){

        if (memberCode <= 0){
            return false;
        }

        int result = memberGradeDAO.updateMembershipGrade(con, memberCode);

        return result > 0;
    }

    // 포인트 적립
    public boolean earnPoint(Connection con, int memberCode, int finalAmount) {

        if (memberCode <= 0 || finalAmount <= 0) {
            return false;
        }

        int rewardRate = memberGradeDAO.selectRewardRate(con, memberCode);

        int earnedPoint = finalAmount * rewardRate / 100;

        // BASIC 회원은 0%로 적립할 포인트가 없으나 정상 처리로 간주
        if (earnedPoint <= 0) {
            return true;
        }

        int result = memberDAO.plusPointBalance(con, memberCode, earnedPoint);

        return result > 0;
    }

    // 결제 취소로 인한 사용된 포인트 복구
    public boolean restoreUsedPoint(Connection con, int memberCode, int usedPoint) {
        if (memberCode <= 0 || usedPoint < 0) {
            return false;
        }
        // 결제 시 사용한 포인트가 없어도 정상 처리
        if (usedPoint == 0) {
            return true;
        }

        int result = memberDAO.plusPointBalance(con, memberCode, usedPoint);

        return result > 0;
    }

    // 포인트 차감(사용)
    public int useAllPoint(Connection con, int memberCode, int paymentAmount) {
        // 반환값: -1은 처리 실패, 0은 사용할 포인트 없음, 양수는 실제 사용 포인트
        if (memberCode <= 0 || paymentAmount <= 0) {
            return -1;
        }
        // 회원 코드만 담은 조회용 DTO로 현재 포인트 잔액을 DB에서 확인
        MemberDTO lookupMember = new MemberDTO(memberCode, "", "");

        MemberDTO memberInfo = memberDAO.selectMember(lookupMember);

        if (memberInfo == null) {
            return -1;
        }

        int pointBalance = memberInfo.getPointBalance();

        // 사용할 포인트가 없을 경우 0 반환
        if (pointBalance <= 0) {
            return 0;
        }

        // 보유 포인트와 결제할 금액 중 작은 값 사용
        int usedPoint = Math.min(pointBalance, paymentAmount);

        int result = memberDAO.minusPointBalance(con, memberCode, usedPoint);

        if (result <= 0) {
            return -1;
        }
        return usedPoint;
    }

    // 결제금액을 기준으로 적립 포인트 계산
    public int calculateEarnedPoint(Connection con, int memberCode, int finalAmount) {
        if (memberCode <= 0 || finalAmount <= 0) {
            return 0;
        }

        int rewardRate = memberGradeDAO.selectRewardRate(con, memberCode);

        return finalAmount * rewardRate / 100;
    }

    // 결제 취소 시 지급된 포인트 회수
    public boolean cancelEarnedPoint(Connection con, int memberCode, int earnedPoint) {
        if (memberCode <= 0 || earnedPoint < 0) {
            return false;
        }

        if (earnedPoint == 0) {
            return true;
        }

        int result = memberDAO.minusPointBalance(con, memberCode, earnedPoint);

        return result > 0;
    }

    // 멤버십 탈퇴
    public boolean withdrawMember(MemberDTO loggedInMember) {
        if (loggedInMember == null) {
            return false;
        }

        if (loggedInMember.getMemberCode() <= 0) {
            return false;
        }

        // 관리자 계정 탈퇴 방지
        if ("admin".equals(loggedInMember.getLoginId())) {
            System.out.println("관리자 계정 탈퇴 불가");
            return false;
        }

        // 주문 외래 키를 유지하기 위해 행을 삭제하지 않고 회원 정보를 탈퇴 상태로 비식별 처리
        int result = memberDAO.withdrawMember(loggedInMember.getMemberCode());

        return result > 0;
    }
}
