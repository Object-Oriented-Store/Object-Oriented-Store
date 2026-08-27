package com.ohgiraffers.store.order.model;

import java.time.LocalDateTime;

// 주문 정보를 전달하기 위한 DTO
public class OrderDTO {

    private int orderCode;                  // 주문 식별 코드
    private int memberCode;                 // 멤버십 코드
    private int originalAmount;             // 할인 전 상품 총액
    private int discountAmount;             // 행사 등을 통해 할인된 금액
    private int finalAmount;                // 실제 결제 금액
    private LocalDateTime orderedAt;        // 주문 생성 일시
    private String orderStatus;             // 주문 진행 상태
    private LocalDateTime refundedAt;       // 환불 완료 일시

    public OrderDTO() {
    }

    public OrderDTO(int orderCode, int memberCode, int originalAmount, int discountAmount, int finalAmount, LocalDateTime orderedAt, String orderStatus, LocalDateTime refundedAt) {
        this.orderCode = orderCode;
        this.memberCode = memberCode;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.refundedAt = refundedAt;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public int getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(int memberCode) {
        this.memberCode = memberCode;
    }

    public int getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(int originalAmount) {
        this.originalAmount = originalAmount;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderCode=" + orderCode +
                ", memberCode=" + memberCode +
                ", originalAmount=" + originalAmount +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", orderedAt=" + orderedAt +
                ", orderStatus='" + orderStatus + '\'' +
                ", refundedAt=" + refundedAt +
                '}';
    }
}
