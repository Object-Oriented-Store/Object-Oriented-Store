package com.ohgiraffers.store.order.view;

import com.ohgiraffers.store.product.model.ProductDTO;

import java.util.Scanner;

public class OrderPurchaseView {

    private final OrderView orderView;

    public OrderPurchaseView(
            Scanner scanner
    ) {

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "입력 도구가 필요합니다."
            );
        }

        this.orderView =
                new OrderView(scanner);
    }

    // 상품 화면에서 선택한 상품을 받아 주문 수량 입력 및 장바구니 처리 화면으로 전달한다.
    public int run(
            int memberCode,
            ProductDTO selectedProduct
    ) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return OrderView.EXIT_PURCHASE;
        }

        if (selectedProduct == null) {
            System.out.println(
                    "선택된 상품정보가 없습니다."
            );
            return OrderView.ADD_MORE_PRODUCT;
        }

        int orderResult =
                orderView.addSelectedProduct(
                        memberCode,
                        selectedProduct.getProductCode(),
                        selectedProduct.getProductName(),
                        selectedProduct.getProductPrice(),
                        selectedProduct.getStockQuantity()
                );

        // 상품 추가를 마치면 주문 담당의 장바구니 관리 화면으로 이동한다.
        if (orderResult
                == OrderView.MOVE_TO_CART) {

            return orderView.run(
                    memberCode
            );
        }

        /*
         * 상품을 더 추가하거나 구매를 종료하는 경우
         * 결과값을 상품 화면에 그대로 전달한다.
         */
        return orderResult;
    }
}