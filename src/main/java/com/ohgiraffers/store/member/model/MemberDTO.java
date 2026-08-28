package com.ohgiraffers.store.member.model;

// 회원 가입, 로그인, 정보 조회 및 수정에 필요한 회원 데이터를 전달하는 객체
public class MemberDTO {
    private int memberCode;         // 멤버십 식별 코드
    private int gradeCode;          // 멤버십 등급 식별코드
    private String loginId;         // 아이디
    private String password;        // 비밀번호
    private String nickname;        // 닉네임
    private int phone;              // 휴대폰번호(010 제외 8자리)
    private int pointBalance;       // 보유 포인트
    private int totalAmount;        // 총 누적 금액

    public MemberDTO() {
    }

    // 로그인 생성자
    public MemberDTO(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }


    // 멤버십 정보 수정 생성자
    public MemberDTO(int memberCode, String password, String nickname, int phone) {
        this.memberCode = memberCode;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

    // 멤버십 가입 생성자
    public MemberDTO(String loginId, String password, String nickname, int phone) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

    // DB에서 조회한 회원의 전체 정보를 담는 생성자
    public MemberDTO(int memberCode, int gradeCode, String loginId, String password, String nickname, int phone, int pointBalance, int totalAmount) {
        this.memberCode = memberCode;
        this.gradeCode = gradeCode;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.pointBalance = pointBalance;
        this.totalAmount = totalAmount;
    }

    // 회원이 직접 (memberCode, pointBalance, totalAmount, gradeCode, LoginId) 수정할 수 없도록 getter만 제공
    public int getMemberCode() {
        return memberCode;
    }

    public int getPointBalance() {
        return pointBalance;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getGradeCode() {
        return gradeCode;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "loginId='" + loginId + '\'' +
                ", memberCode=" + memberCode +
                ", gradeCode=" + gradeCode +
                ", nickname='" + nickname + '\'' +
                ", phone=" + phone +
                ", pointBalance=" + pointBalance +
                ", totalAmount=" + totalAmount +
                '}';
    }
}


