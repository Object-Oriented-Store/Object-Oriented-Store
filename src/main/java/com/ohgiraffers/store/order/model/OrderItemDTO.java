package com.ohgiraffers.store.order.model;

public class OrderItemDTO {

    private int orderCode;              // 주문 식별 번호
    private int productCode;            // 상품 식별 코드
    private int quantity;               // 상품 주문 수량

    public OrderItemDTO() {
    }

    public OrderItemDTO(int orderCode, int productCode, int quantity) {
        this.orderCode = orderCode;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "orderCode=" + orderCode +
                ", productCode=" + productCode +
                ", quantity=" + quantity +
                '}';
    }
}
