package com.ohgiraffers.store.order.view;

import com.ohgiraffers.store.order.controller.OrderController;
import com.ohgiraffers.store.order.model.OrderDTO;
import com.ohgiraffers.store.order.model.OrderItemDTO;

import java.util.List;
import java.util.Scanner;

public class OrderView {

    public static final int EXIT_PURCHASE = 0;
    public static final int MOVE_TO_CART = 1;
    public static final int ADD_MORE_PRODUCT = 2;
    public static final int REQUEST_PAYMENT = 3;

    private final Scanner scanner;
    private final OrderController orderController;

    public OrderView() {
        this(new Scanner(System.in));
    }

    public OrderView(
            Scanner scanner
    ) {

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "입력 도구가 필요합니다."
            );
        }

        this.scanner = scanner;
        this.orderController = new OrderController();
    }

    // 로그인한 회원의 주문 메뉴 실행
    public int run(
            int memberCode
    ) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return EXIT_PURCHASE;
        }

        while (true) {
            printOrderMenu();

            int menuNumber =
                    readInt("메뉴를 선택하세요: ");

            try {
                switch (menuNumber) {
                    case 1 -> {
                        showPendingOrderItems(memberCode);
                        showPendingOrderAmount(memberCode);
                    }

                    case 2 -> {
                        updateOrderItemQuantity(memberCode);
                        showPendingOrderAmount(memberCode);
                    }

                    case 3 -> {
                        deleteOrderItem(memberCode);
                        showPendingOrderAmount(memberCode);
                    }

                    case 4 -> {
                        deleteAllOrderItems(memberCode);
                        showPendingOrderAmount(memberCode);
                    }

                    case 5 -> {

                        int paymentResult =
                                requestPayment(memberCode);

                        if (paymentResult == REQUEST_PAYMENT) {
                            return REQUEST_PAYMENT;
                        }

                        // 결제 취소 시 return하지 않고
                        // while 문을 반복해 장바구니 메뉴를 다시 출력
                    }

                    case 0 -> {
                        System.out.println(
                                "이전 화면으로 돌아갑니다."
                        );
                        return EXIT_PURCHASE;
                    }

                    default -> System.out.println(
                            "목록에 있는 메뉴 번호를 입력해주세요."
                    );
                }

            } catch (RuntimeException e) {
                System.out.println(
                        "주문 처리 중 오류가 발생했습니다: "
                                + e.getMessage()
                );
            }
        }
    }

    // 주문 메뉴 출력
    private void printOrderMenu() {

        System.out.println();
        System.out.println("========== 장바구니 메뉴 ==========");
        System.out.println("1. 주문 상품 조회");
        System.out.println("2. 주문 상품 수량 수정");
        System.out.println("3. 주문 상품 일부 삭제");
        System.out.println("4. 주문 상품 전체 삭제");
        System.out.println("5. 결제하기");
        System.out.println("0. 이전 화면");
        System.out.println("==================================");
    }

    // 숫자 입력 처리
    private int readInt(String message) {

        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "숫자로 입력해주세요."
                );
            }
        }
    }

    private boolean readYesOrNo(
            String message
    ) {

        while (true) {
            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (input.equalsIgnoreCase("y")) {
                return true;
            }

            if (input.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println(
                    "Y 또는 N으로 입력해주세요."
            );
        }
    }

    private void showPendingOrderItems(
            int memberCode
    ) {

        List<OrderItemDTO> orderItems =
                orderController.findPendingOrderItems(
                        memberCode
                );

        if (orderItems.isEmpty()) {
            System.out.println(
                    "현재 주문에 담긴 상품이 없습니다."
            );
            return;
        }

        System.out.println();
        System.out.println("========== 주문 상품 목록 ==========");

        for (OrderItemDTO orderItem : orderItems) {
            System.out.println(
                    "상품번호: " + orderItem.getProductCode()
                            + ", 주문수량: " + orderItem.getQuantity()
            );
        }

        System.out.println("===================================");
    }

    public int addSelectedProduct(
            int memberCode,
            int productCode,
            String productName,
            int productPrice,
            int stockQuantity
    ) {

        if (memberCode <= 0 || productCode <= 0) {
            System.out.println(
                    "회원 또는 상품 정보가 올바르지 않습니다."
            );
            return EXIT_PURCHASE;
        }

        System.out.println();
        System.out.println("========== 선택 상품 ==========");
        System.out.println("상품명: " + productName);
        System.out.println("가격: " + productPrice + "원");
        System.out.println("현재 재고: " + stockQuantity + "개");
        System.out.println("==============================");

        int quantity =
                readInt("구매 수량을 입력하세요: ");

        if (quantity <= 0) {
            System.out.println(
                    "구매 수량은 1개 이상이어야 합니다."
            );
            return ADD_MORE_PRODUCT;
        }

        if (quantity > stockQuantity) {
            System.out.println(
                    "재고가 부족합니다. 현재 재고: "
                            + stockQuantity + "개"
            );
            return ADD_MORE_PRODUCT;
        }

        boolean added =
                orderController.addOrderItem(
                        memberCode,
                        productCode,
                        quantity
                );

        if (!added) {
            System.out.println(
                    "주문 상품 추가에 실패했습니다."
            );
            return ADD_MORE_PRODUCT;
        }

        System.out.println(
                productName + " 상품을 추가했습니다."
        );

        showPendingOrderAmount(memberCode);

        boolean addMore =
                readYesOrNo(
                        "상품을 더 추가하시겠습니까? (Y/N): "
                );

        if (addMore) {
            return ADD_MORE_PRODUCT;
        }

        return MOVE_TO_CART;
    }

    // 현재 PENDING 주문의 누적 금액 출력
    private void showPendingOrderAmount(
            int memberCode
    ) {

        OrderDTO pendingOrder =
                orderController.findPendingOrder(
                        memberCode
                );

        if (pendingOrder == null) {
            System.out.println(
                    "현재 주문 정보를 찾을 수 없습니다."
            );
            return;
        }

        System.out.println();
        System.out.println("========== 현재 주문 금액 ==========");
        System.out.println(
                "상품 금액: "
                        + pendingOrder.getOriginalAmount()
                        + "원"
        );
        System.out.println(
                "할인 금액: "
                        + pendingOrder.getDiscountAmount()
                        + "원"
        );
        System.out.println(
                "결제 예정 금액: "
                        + pendingOrder.getFinalAmount()
                        + "원"
        );
        System.out.println("==================================");
    }

    // 주문 상품 수량 수정
    private void updateOrderItemQuantity(
            int memberCode
    ) {

        int productCode =
                readInt("수량을 변경할 상품번호를 입력하세요: ");

        int quantity =
                readInt("변경할 주문수량을 입력하세요: ");

        boolean result =
                orderController.updateOrderItemQuantity(
                        memberCode,
                        productCode,
                        quantity
                );

        if (result) {
            System.out.println(
                    "주문수량이 수정되었습니다."
            );
        } else {
            System.out.println(
                    "주문수량을 수정하지 못했습니다."
            );
        }
    }

    // 선택한 주문 상품 삭제
    private void deleteOrderItem(
            int memberCode
    ) {

        int productCode =
                readInt("삭제할 상품번호를 입력하세요: ");

        boolean result =
                orderController.deleteOrderItem(
                        memberCode,
                        productCode
                );

        if (result) {
            System.out.println(
                    "선택한 주문 상품이 삭제되었습니다."
            );
        } else {
            System.out.println(
                    "주문 상품을 삭제하지 못했습니다."
            );
        }
    }

    // 주문 상품 전체 삭제
    private void deleteAllOrderItems(
            int memberCode
    ) {

        boolean confirmed =
                readYesOrNo(
                        "주문 상품을 모두 삭제하시겠습니까? (Y/N): "
                );

        if (!confirmed) {
            System.out.println(
                    "전체 삭제를 취소했습니다."
            );
            return;
        }

        boolean result =
                orderController.deleteAllOrderItems(
                        memberCode
                );

        if (result) {
            System.out.println(
                    "주문 상품이 모두 삭제되었습니다."
            );
        } else {
            System.out.println(
                    "주문 상품 전체 삭제에 실패했습니다."
            );
        }
    }

    private int requestPayment(
            int memberCode
    ) {

        List<OrderItemDTO> orderItems =
                orderController.findPendingOrderItems(
                        memberCode
                );

        if (orderItems.isEmpty()) {
            System.out.println(
                    "결제할 주문 상품이 없습니다."
            );
            return MOVE_TO_CART;
        }

        showPendingOrderItems(memberCode);
        showPendingOrderAmount(memberCode);

        boolean confirmed =
                readYesOrNo(
                        "결제 단계로 이동하시겠습니까? (Y/N): "
                );

        if (confirmed) {
            return REQUEST_PAYMENT;
        }

        return MOVE_TO_CART;
    }
}