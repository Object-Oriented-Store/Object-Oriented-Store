package com.ohgiraffers.store.order.controller;

import com.ohgiraffers.store.order.model.OrderItemDTO;
import com.ohgiraffers.store.order.service.OrderService;

import java.util.List;

public class OrderController {

    private final OrderService orderService;

    public OrderController() {
        this.orderService = new OrderService();
    }

    // 주문에 상품 추가 요청
    public boolean addOrderItem(
            int memberCode,
            int productCode,
            int quantity
    ) {

        return orderService.addOrderItem(
                memberCode, productCode, quantity
        );
    }

    // 주문 상품 수량 수정 요청
    public boolean updateOrderItemQuantity(
            int memberCode,
            int productCode,
            int quantity
    ) {

        return orderService.updateOrderItemQuantity(
                memberCode, productCode, quantity
        );
    }

    // 선택한 주문 상품 삭제 요청
    public boolean deleteOrderItem(
            int memberCode,
            int productCode
    ) {

        return orderService.deleteOrderItem(
                memberCode, productCode
        );
    }

    // 주문 상품 전체 삭제 요청
    public boolean deleteAllOrderItems(
            int memberCode
    ) {

        return orderService.deleteAllOrderItems(
                memberCode
        );
    }

    // 결제 전 주문 상품 조회 요청
    public List<OrderItemDTO> findPendingOrderItems(
            int memberCode
    ) {

        return orderService.findPendingOrderItems(
                memberCode
        );
    }
}
