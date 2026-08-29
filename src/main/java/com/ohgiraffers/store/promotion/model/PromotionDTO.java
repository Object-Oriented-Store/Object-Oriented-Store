package com.ohgiraffers.store.promotion.model;

public class PromotionDTO {
    private int promotionCode;
    private String promotionName;
    private  String promotionColumn;
    private int discountValue;
    private String promotionStatus;


    public void PromotionDTO() {}

    public void PromotionDTO(int promotionCode, String promotionName, String promotionColumn, int discountValue, String promotionStatu) {
        this.promotionCode=promotionCode;
        this.promotionName=promotionName;
        this.promotionColumn=promotionColumn;
        this.discountValue=discountValue;
        this.promotionStatus=promotionStatus;
    }

    public int getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(int promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionColumn() {
        return promotionColumn;
    }

    public void setPromotionColumn(String promotionColumn) {
        this.promotionColumn = promotionColumn;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }

    public String getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    @Override
    public String toString() {
        return "PromotionDTO = {promotionCode= " + promotionCode
                + ", promotionName= " + promotionName
                + ", promotionColumn= " + promotionColumn
                + ", discountValue= " + discountValue
                + ", promotionStatus= " + promotionStatus
                +"}";
    }
}
