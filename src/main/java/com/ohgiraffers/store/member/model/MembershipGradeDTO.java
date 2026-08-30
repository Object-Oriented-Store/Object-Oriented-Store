package com.ohgiraffers.store.member.model;

// 총 누적 구매 금액에 따른 등급 적용과 포인트 적립에 사용하는 데이터 객체
public class MembershipGradeDTO {

    private int gradeCode;          // 멤버십 등급 식별 코드
    private String gradeName;       // 멤버십 등급 이름
    private int minPurchaseAmount;  // 등급 적용 최소 구매 금액
    private int rewardRate;         // 구매 금액에 적용되는 적립율


    // DB에서 조회한 멤버십 등급의 전체 정보를 담는 생성자
    public MembershipGradeDTO(int gradeCode, String gradeName, int minPurchaseAmount, int rewardRate) {
        this.gradeCode = gradeCode;
        this.gradeName = gradeName;
        this.minPurchaseAmount = minPurchaseAmount;
        this.rewardRate = rewardRate;
    }


    // 수정이 필요하지 않기에 getter만 생성
    public int getGradeCode() {
        return gradeCode;
    }

    public String getGradeName() {
        return gradeName;
    }

    public int getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public int getRewardRate() {
        return rewardRate;
    }

    @Override
    public String toString() {
        return "MembershipGradeDTO{" +
                "gradeCode=" + gradeCode +
                ", gradeName='" + gradeName + '\'' +
                ", minPurchaseAmount=" + minPurchaseAmount +
                ", rewardRate=" + rewardRate +
                '}';
    }
}
