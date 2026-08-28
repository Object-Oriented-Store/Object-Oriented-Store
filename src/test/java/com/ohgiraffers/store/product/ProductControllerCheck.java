package com.ohgiraffers.store.product;

import com.ohgiraffers.store.product.controller.ProductController;

import java.sql.SQLException;

/**
 * 화면 대신 Controller를 직접 호출해 상품 조회 흐름을 확인하는 실행용 클래스이다.
 * JUnit 단위 테스트를 배우기 전까지 main()으로 기능을 빠르게 확인할 수 있다.
 */
public final class ProductControllerCheck {

    private ProductControllerCheck() {
    }

    public static void main(String[] args) {
        ProductController controller = new ProductController();

        try {
            System.out.println("전체 상품 수: " + controller.findAllProducts().size());
            System.out.println("상품코드 1: " + controller.findProductByCode(1));
            System.out.println("카테고리 2 상품 수: "
                    + controller.findProductsByCategory(2).size());
            System.out.println("'라면' 검색 결과 수: "
                    + controller.searchProductsByName("라면").size());

            /* 잘못된 입력이 DAO까지 내려가기 전에 Service에서 차단되는지 확인한다. */
            try {
                controller.findProductByCode(0);
            } catch (IllegalArgumentException exception) {
                System.out.println("입력값 검사 성공: " + exception.getMessage());
            }

            System.out.println("구매 가능 상품 검사: "
                    + controller.validateProductPurchase(6, 1).getProductName());

            try {
                /* 테스트 상품 50번은 재고가 0개이므로 구매할 수 없다. */
                controller.validateProductPurchase(50, 1);
            } catch (IllegalStateException exception) {
                System.out.println("품절 상품 구매 차단 성공: " + exception.getMessage());
            }

            try {
                /* 상품 6번 재고는 100개이므로 101개 구매를 차단해야 한다. */
                controller.validateProductPurchase(6, 101);
            } catch (IllegalStateException exception) {
                System.out.println("재고부족 구매 차단 성공: " + exception.getMessage());
            }
        } catch (SQLException exception) {
            System.err.println("상품 기능 확인 실패: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
