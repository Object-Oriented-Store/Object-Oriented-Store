package com.ohgiraffers.store.payment.model;

public class PaymentDTO {

    private int payCode;
    private int orderCode;
    private int memberCode;
    private String paymentMethod;
    private int originalAmount;
    private int discountAmount;
    private int pointUse;
    private int finalAmount;
    private String paymentStatus;

    public PaymentDTO() {
    }

    public PaymentDTO(int payCode, int orderCode, int memberCode, String paymentMethod, int originalAmount,
                      int discountAmount, int pointUse, int finalAmount, String paymentStatus) {
        this.payCode = payCode;
        this.orderCode = orderCode;
        this.memberCode = memberCode;
        this.paymentMethod = paymentMethod;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.pointUse = pointUse;
        this.finalAmount = finalAmount;
        this.paymentStatus = paymentStatus;
    }

    public int getPayCode() {
        return payCode;
    }

    public void setPayCode(int payCode) {
        this.payCode = payCode;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public int getPointUse() {
        return pointUse;
    }

    public void setPointUse(int pointUse) {
        this.pointUse = pointUse;
    }

    public int getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "payCode=" + payCode +
                ", orderCode=" + orderCode +
                ", memberCode=" + memberCode +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", originalAmount=" + originalAmount +
                ", discountAmount=" + discountAmount +
                ", pointUse=" + pointUse +
                ", finalAmount=" + finalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
