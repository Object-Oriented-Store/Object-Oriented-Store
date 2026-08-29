package com.ohgiraffers.store.payment.view;

import com.ohgiraffers.store.payment.controller.PaymentController;
import com.ohgiraffers.store.payment.model.PaymentDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PaymentView {

    private final Scanner scanner;
    private final PaymentController paymentController;

    public PaymentView() {
        this.scanner = new Scanner(System.in);
        this.paymentController = new PaymentController();
    }

    // 로그인한 회원의 결제 메뉴 실행
    public void run(int memberCode) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return;
        }

        while (true) {
            printPaymentMenu();

            int menuNumber =
                    readInt("메뉴를 선택하세요: ");

            switch (menuNumber) {
                case 1 -> showAllPayments(memberCode);
                case 2 -> showPaymentByPayCode(memberCode);
                case 3 -> cancelPayment(memberCode);

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
        }
    }

    // 결제 메뉴 출력
    private void printPaymentMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("              결제 메뉴");
        System.out.println("========================================");
        System.out.println("1. 나의 전체 결제 내역 조회");
        System.out.println("2. 결제 한 건 조회");
        System.out.println("3. 결제 취소");
        System.out.println("0. 이전 화면으로");
        System.out.println("========================================");
    }

    // 로그인한 회원의 전체 결제 내역 출력
    private void showAllPayments(int memberCode) {

        try {
            List<PaymentDTO> payments =
                    paymentController.findAllPaymentsByMemberCode(
                            memberCode
                    );

            if (payments.isEmpty()) {
                System.out.println(
                        "조회된 결제 내역이 없습니다."
                );
                return;
            }

            System.out.println();
            System.out.println("========== 나의 결제 내역 ==========");

            for (PaymentDTO payment : payments) {
                printPayment(payment);
            }

        } catch (SQLException exception) {
            System.out.println(
                    "결제 내역 조회 중 DB 오류가 발생했습니다: "
                            + exception.getMessage()
            );
        }
    }

    // 결제번호로 본인의 결제 한 건 조회
    private void showPaymentByPayCode(int memberCode) {

        int payCode =
                readInt("조회할 결제번호를 입력하세요: ");

        try {
            PaymentDTO payment =
                    paymentController.findPaymentByPayCode(
                            memberCode,
                            payCode
                    );

            if (payment == null) {
                System.out.println(
                        "해당 결제 내역을 찾을 수 없습니다."
                );
                return;
            }

            System.out.println();
            System.out.println("========== 결제 상세 ==========");

            printPayment(payment);

        } catch (SQLException exception) {
            System.out.println(
                    "결제 조회 중 DB 오류가 발생했습니다: "
                            + exception.getMessage()
            );
        }
    }

    // 선택한 결제 취소
    private void cancelPayment(int memberCode) {

        int payCode =
                readInt("취소할 결제번호를 입력하세요: ");

        try {
            PaymentDTO payment =
                    paymentController.findPaymentByPayCode(
                            memberCode,
                            payCode
                    );

            if (payment == null) {
                System.out.println(
                        "취소할 결제 내역을 찾을 수 없습니다."
                );
                return;
            }

            if (!"COMPLETED".equals(
                    payment.getPaymentStatus()
            )) {
                System.out.println(
                        "완료된 결제만 취소할 수 있습니다."
                );
                return;
            }

            System.out.println();
            System.out.println("========== 취소할 결제 ==========");
            printPayment(payment);

            if (!readYesOrNo(
                    "이 결제를 취소하시겠습니까? (Y/N): "
            )) {
                System.out.println(
                        "결제 취소를 중단했습니다."
                );
                return;
            }

            boolean canceled =
                    paymentController.cancelPayment(
                            memberCode,
                            payCode
                    );

            if (canceled) {
                System.out.println(
                        "결제가 정상적으로 취소됐습니다."
                );
            } else {
                System.out.println(
                        "결제를 취소하지 못했습니다."
                );
            }

        } catch (SQLException exception) {
            System.out.println(
                    "결제 취소 중 DB 오류가 발생했습니다: "
                            + exception.getMessage()
            );
        }
    }

    // 결제 정보 한 건 출력
    private void printPayment(PaymentDTO payment) {

        System.out.printf(
                "결제번호=%d | 주문번호=%d | 결제방식=%s | "
                        + "할인 전=%d원 | 할인=%d원 | 사용 포인트=%d | "
                        + "최종 결제금액=%d원 | 상태=%s%n",
                payment.getPayCode(),
                payment.getOrderCode(),
                payment.getPaymentMethod(),
                payment.getOriginalAmount(),
                payment.getDiscountAmount(),
                payment.getPointUse(),
                payment.getFinalAmount(),
                payment.getPaymentStatus()
        );
    }

    // Y 또는 N 입력
    private boolean readYesOrNo(String message) {

        while (true) {
            System.out.print(message);

            String answer =
                    scanner.nextLine().trim();

            if ("Y".equalsIgnoreCase(answer)) {
                return true;
            }

            if ("N".equalsIgnoreCase(answer)) {
                return false;
            }

            System.out.println(
                    "Y 또는 N으로 입력해주세요."
            );
        }
    }

    // 숫자 입력
    private int readInt(String message) {

        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException exception) {
                System.out.println(
                        "숫자로 입력해주세요."
                );
            }
        }
    }
}
