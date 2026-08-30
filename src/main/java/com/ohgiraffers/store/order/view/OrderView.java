package com.ohgiraffers.store.order.view;

import com.ohgiraffers.store.order.controller.OrderController;
import com.ohgiraffers.store.order.model.OrderItemDTO;

import java.util.List;
import java.util.Scanner;

public class OrderView {

    private final Scanner scanner;
    private final OrderController orderController;

    public OrderView() {
        this.scanner = new Scanner(System.in);
        this.orderController = new OrderController();
    }

    // 로그인한 회원의 주문 메뉴 실행
    public void run(
            int memberCode
    ) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return;
        }

        while (true) {
            printOrderMenu();

            int menuNumber =
                    readInt("메뉴를 선택하세요: ");

            try {
                switch (menuNumber) {
                    case 1 -> showPendingOrderItems(memberCode);
                    case 2 -> addOrderItem(memberCode);
                    case 3 -> updateOrderItemQuantity(memberCode);
                    case 4 -> deleteOrderItem(memberCode);
                    case 5 -> deleteAllOrderItems(memberCode);
                    case 0 -> {
                        System.out.println(
                                "이전 화면으로 돌아갑니다."
                        );
                        return;
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
        System.out.println("========== 주문 메뉴 ==========");
        System.out.println("1. 주문 상품 조회");
        System.out.println("2. 주문 상품 추가");
        System.out.println("3. 주문 상품 수량 수정");
        System.out.println("4. 선택한 주문 상품 삭제");
        System.out.println("5. 주문 상품 전체 삭제");
        System.out.println("0. 이전 화면");
        System.out.println("==============================");
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

    // 주문 상품 추가
    private void addOrderItem(
            int memberCode
    ) {

        int productCode =
                readInt("추가할 상품번호를 입력하세요: ");

        int quantity =
                readInt("주문수량을 입력하세요: ");

        boolean result =
                orderController.addOrderItem(
                        memberCode,
                        productCode,
                        quantity
                );

        if (result) {
            System.out.println(
                    "주문 상품이 추가되었습니다."
            );
        } else {
            System.out.println(
                    "주문 상품을 추가하지 못했습니다."
            );
        }
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

        System.out.print(
                "주문 상품을 모두 삭제하시겠습니까? (y/n): "
        );

        String answer =
                scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("y")) {
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
}
