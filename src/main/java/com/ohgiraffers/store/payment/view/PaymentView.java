package com.ohgiraffers.store.payment.view;

import com.ohgiraffers.store.order.controller.OrderController;
import com.ohgiraffers.store.order.model.OrderDTO;
import com.ohgiraffers.store.payment.controller.PaymentController;
import com.ohgiraffers.store.payment.model.PaymentDTO;
import com.ohgiraffers.store.member.controller.MemberController;
import com.ohgiraffers.store.member.model.MemberDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PaymentView {

    private final Scanner scanner;
    private final PaymentController paymentController;
    private final OrderController orderController;
    private final MemberController memberController;

    public PaymentView() {
        this.scanner = new Scanner(System.in);
        this.paymentController =
                new PaymentController();
        this.orderController =
                new OrderController();
        this.memberController =
                new MemberController();
    }

    // 주문 화면과 같은 Scanner를 사용하기 위한 생성자
    public PaymentView(Scanner scanner) {

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "입력 도구가 필요합니다."
            );
        }

        this.scanner = scanner;
        this.paymentController =
                new PaymentController();
        this.orderController =
                new OrderController();
        this.memberController =
                new MemberController();
    }

    // 주문 완료 후 신규 결제 진행
    public boolean checkout(int memberCode) {

        if (memberCode <= 0) {
            System.out.println(
                    "올바른 회원정보가 필요합니다."
            );
            return false;
        }

        // 로그인 회원의 결제 전 PENDING 주문 조회
        OrderDTO pendingOrder =
                orderController.findPendingOrder(
                        memberCode
                );

        if (pendingOrder == null) {
            System.out.println(
                    "결제할 주문이 없습니다."
            );
            return false;
        }

        if (pendingOrder.getFinalAmount() <= 0) {
            System.out.println(
                    "결제할 주문 상품이 없습니다."
            );
            return false;
        }

        String paymentMethod;
        int pointUse;

        // 포인트 결제가 불가능하면 결제 방식 선택 화면으로 돌아간다.
        while (true) {

            paymentMethod =
                    selectPaymentMethod();

            pointUse = 0;

            // 포인트 결제가 아니면 선택을 확정하고 반복문 종료
            if (!"POINT".equals(paymentMethod)) {
                break;
            }

            // 포인트 결제를 선택한 경우 로그인 회원의 보유 포인트 조회
            MemberDTO lookupMember =
                    new MemberDTO(
                            memberCode,
                            "",
                            ""
                    );

            MemberDTO memberInfo =
                    memberController.selectMember(lookupMember);

            // 회원 조회 실패는 결제 방식의 문제가 아니므로 결제를 종료
            if (memberInfo == null) {
                System.out.println(
                        "회원 정보를 조회할 수 없습니다."
                );
                return false;
            }

            int pointBalance = memberInfo.getPointBalance();

            int paymentAmount = pendingOrder.getFinalAmount();

            // 포인트가 없으면 다른 결제 방식을 다시 선택
            if (pointBalance <= 0) {
                System.out.println(
                        "사용할 수 있는 포인트가 없습니다."
                );
                System.out.println(
                        "다른 결제 방식을 선택해주세요."
                );
                continue;
            }

            // 포인트가 부족하면 다른 결제 방식을 다시 선택
            if (pointBalance < paymentAmount) {
                System.out.println(
                        "보유 포인트가 결제금액보다 부족합니다."
                );
                System.out.println(
                        "보유 포인트: "
                                + pointBalance
                                + "점"
                );
                System.out.println(
                        "필요 포인트: "
                                + paymentAmount
                                + "점"
                );
                System.out.println(
                        "다른 결제 방식을 선택해주세요."
                );
                continue;
            }

            pointUse = paymentAmount;

            System.out.println(
                    "보유 포인트: "
                            + pointBalance
                            + "점"
            );
            System.out.println(
                    "사용할 포인트: "
                            + pointUse
                            + "점"
            );

            if (!readYesOrNo(
                    "포인트를 사용하여 전액 결제하시겠습니까? (Y/N): "
            )) {
                System.out.println(
                        "포인트 결제를 취소했습니다."
                );
                System.out.println(
                        "다른 결제 방식을 선택해주세요."
                );
                continue;
            }

            // 사용할 수 있는 포인트가 충분하고 사용에도 동의했으므로 선택 확정
            break;
        }

        int finalAmount =
                pendingOrder.getFinalAmount()
                        - pointUse;

        PaymentDTO payment =
                new PaymentDTO(
                        pendingOrder.getOrderCode(),
                        memberCode,
                        paymentMethod,
                        pendingOrder.getOriginalAmount(),
                        pendingOrder.getDiscountAmount(),
                        pointUse,
                        finalAmount,
                        "PENDING"
                );

        System.out.println();
        System.out.println(
                "========== 결제 정보 =========="
        );
        System.out.println(
                "주문번호: "
                        + pendingOrder.getOrderCode()
        );
        System.out.println(
                "결제 방식: "
                        + paymentMethod
        );
        System.out.println(
                "할인 전 금액: "
                        + pendingOrder.getOriginalAmount()
                        + "원"
        );
        System.out.println(
                "할인 금액: "
                        + pendingOrder.getDiscountAmount()
                        + "원"
        );
        System.out.println(
                "사용 포인트: "
                        + pointUse
        );
        System.out.println(
                "최종 결제금액: "
                        + finalAmount
                        + "원"
        );
        System.out.println(
                "=============================="
        );

        if (!readYesOrNo(
                "결제를 진행하시겠습니까? (Y/N): "
        )) {
            System.out.println(
                    "결제를 취소했습니다."
            );
            return false;
        }

        try {
            boolean result =
                    paymentController.registerPayment(
                            payment
                    );

            if (result) {
                System.out.println(
                        "결제가 완료되었습니다.");

                printPayment(payment);
                return true;
            }

            System.out.println(
                    "결제 처리에 실패했습니다."
            );
            return false;

        } catch (SQLException exception) {
            System.out.println(
                    "결제 처리 중 DB 오류가 발생했습니다: "
                            + exception.getMessage()
            );
            return false;

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "결제 정보가 올바르지 않습니다: "
                            + exception.getMessage()
            );
            return false;
        }
    }

    // 로그인한 회원의 결제 내역 메뉴 실행
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
                case 1:
                    showAllPayments(memberCode);
                    break;

                case 2:
                    showPaymentByPayCode(memberCode);
                    break;

                case 3:
                    cancelPayment(memberCode);
                    break;

                case 0:
                    System.out.println(
                            "이전 화면으로 돌아갑니다."
                    );
                    return;

                default:
                    System.out.println(
                            "목록에 있는 메뉴 번호를 입력해주세요."
                    );
            }
        }
    }

    // 결제 내역 메뉴 출력
    private void printPaymentMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "              결제 메뉴"
        );
        System.out.println(
                "========================================"
        );
        System.out.println(
                "1. 나의 전체 결제 내역 조회"
        );
        System.out.println(
                "2. 결제 한 건 조회"
        );
        System.out.println(
                "3. 결제 취소"
        );
        System.out.println(
                "0. 이전 화면으로"
        );
        System.out.println(
                "========================================"
        );
    }

    // 로그인한 회원의 전체 결제 내역 출력
    private void showAllPayments(int memberCode) {

        try {
            List<PaymentDTO> payments =
                    paymentController
                            .findAllPaymentsByMemberCode(
                                    memberCode
                            );

            if (payments.isEmpty()) {
                System.out.println(
                        "조회된 결제 내역이 없습니다."
                );
                return;
            }

            System.out.println();
            System.out.println(
                    "========== 나의 결제 내역 =========="
            );

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

    // 결제번호로 로그인한 회원의 결제 한 건 조회
    private void showPaymentByPayCode(
            int memberCode
    ) {

        int payCode =
                readInt(
                        "조회할 결제번호를 입력하세요: "
                );

        try {
            PaymentDTO payment =
                    paymentController
                            .findPaymentByPayCode(
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
            System.out.println(
                    "========== 결제 상세 =========="
            );

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
                readInt(
                        "취소할 결제번호를 입력하세요: "
                );

        try {
            PaymentDTO payment =
                    paymentController
                            .findPaymentByPayCode(
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
            System.out.println(
                    "========== 취소할 결제 =========="
            );

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

    // 결제 방식 선택
    private String selectPaymentMethod() {

        while (true) {
            System.out.println();
            System.out.println(
                    "========== 결제 방식 =========="
            );
            System.out.println(
                    "1. 카드"
            );
            System.out.println(
                    "2. 카카오페이"
            );
            System.out.println(
                    "3. 휴대폰 결제"
            );
            System.out.println(
                    "4. 포인트 결제"
            );
            System.out.println(
                    "=============================="
            );

            int menuNumber =
                    readInt(
                            "결제 방식을 선택하세요: "
                    );

            switch (menuNumber) {
                case 1:
                    return "CARD";

                case 2:
                    return "KAKAO_PAY";

                case 3:
                    return "MOBILE";

                case 4:
                    return "POINT";

                default:
                    System.out.println(
                            "1~4 사이의 숫자를 입력해주세요."
                    );
            }
        }
    }

    // 결제 완료 후 이동할 화면 선택
    public boolean selectAfterPaymentMenu() {

        while (true) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("          결제가 완료되었습니다.");
            System.out.println("========================================");
            System.out.println("1. 멤버십 초기화면으로");
            System.out.println("2. 프로그램 종료");
            System.out.println("----------------------------------------");

            int menuNumber =
                    readInt("메뉴를 선택하세요: ");

            switch (menuNumber) {
                case 1:
                    System.out.println("멤버십 초기화면으로 이동합니다.");
                    return true;

                case 2:
                    System.out.println("프로그램을 종료합니다.");
                    return false;

                default:
                    System.out.println("1 또는 2를 입력해주세요.");
            }
        }
    }

    // 결제 정보 한 건 출력
    private void printPayment(PaymentDTO payment) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          OBJECT-ORIENTED STORE");
        System.out.println("                 영수증");
        System.out.println("========================================");
        System.out.printf("결제 번호        : %d%n", payment.getPayCode());
        System.out.printf("주문 번호        : %d%n", payment.getOrderCode());
        System.out.printf("결제 방식        : %s%n", payment.getPaymentMethod());
        System.out.println("----------------------------------------");
        System.out.printf("상품 총액        : %,d원%n", payment.getOriginalAmount());
        System.out.printf("할인 금액        : -%,d원%n", payment.getDiscountAmount());
        System.out.printf("사용 포인트      : -%,dP%n", payment.getPointUse());
        System.out.println("----------------------------------------");
        System.out.printf("최종 결제 금액   : %,d원%n", payment.getFinalAmount());
        System.out.printf("결제 상태        : %s%n", payment.getPaymentStatus());
        System.out.println("========================================");
        System.out.println("          이용해 주셔서 감사합니다.");
        System.out.println("========================================");
    }

    // Y 또는 N 입력
    private boolean readYesOrNo(
            String message
    ) {

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
    private int readInt(
            String message
    ) {

        while (true) {
            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

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