package com.ohgiraffers.store.payment.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.member.service.MemberService;
import com.ohgiraffers.store.order.repository.OrderDAO;
import com.ohgiraffers.store.payment.model.PaymentDTO;
import com.ohgiraffers.store.payment.repository.PaymentDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final MemberService memberService;
    private final OrderDAO orderDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.memberService = new MemberService();
        this.orderDAO = new OrderDAO();
    }

    // 로그인한 회원의 전체 결제 내역 조회
    public List<PaymentDTO> findAllPaymentsByMemberCode(
            int memberCode
    ) throws SQLException {

        if (memberCode <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            return paymentDAO.findAllByMemberCode(
                    connection, memberCode
            );
        }
    }

    // 로그인한 회원의 결제 한 건 조회
    public PaymentDTO findPaymentByPayCode(
            int memberCode,
            int payCode
    ) throws SQLException {

        if (memberCode <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        if (payCode <= 0) {
            throw new IllegalArgumentException(
                    "결제번호는 1 이상이어야 합니다."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            PaymentDTO payment =
                    paymentDAO.findByPayCode(
                            connection, payCode
                    );

            if (payment == null) {
                return null;
            }

            if (payment.getMemberCode() != memberCode) {
                return null;
            }

            return payment;
        }
    }

    // 결제 정보 등록
    public boolean registerPayment(
            PaymentDTO payment
    ) throws SQLException {

        validatePayment(payment);

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                payment.setPaymentStatus("COMPLETED");

                // 결제 등록 성공 여부 검증
                int paymentResult = paymentDAO.insertPayment(connection, payment);

                if (paymentResult != 1) {
                    connection.rollback();
                    return false;
                }

                int pointUsed = 0;

                // 포인트 실제 차감 결과 검증
                if (payment.getPointUse() > 0) {

                    pointUsed = memberService.useAllPoint(connection, payment.getMemberCode(), payment.getPointUse());

                    if (pointUsed != payment.getPointUse()) {
                        connection.rollback();
                        return false;
                    }
                }

                // 포인트 사용으로 총 결제 금액이 0원일 시 누적 금액 증가 및 포인트 적립 미처리
                if (payment.getFinalAmount() > 0) {

                    // 누적 금액 증가 및 등급 변경 성공 여부 검증
                    boolean amountUpdated = memberService.plusTotalAmount(
                            connection, payment.getMemberCode(), payment.getFinalAmount());

                    if (!amountUpdated) {
                        connection.rollback();
                        return false;
                    }

                    // 포인트 적립 여부 검증
                    boolean pointEarned = memberService.earnPoint(
                            connection, payment.getMemberCode(), payment.getFinalAmount());

                    if (!pointEarned) {
                        connection.rollback();
                        return false;
                    }
                }

                // 결제가 완료된 주문을 PENDING에서 PAID로 변경
                int orderStatusResult =
                        orderDAO.updateOrderStatusToPaid(
                                connection,
                                payment.getOrderCode(),
                                payment.getMemberCode()
                        );

                if (orderStatusResult != 1) {
                    connection.rollback();
                    return false;
                }

                // 문제 없을 시 commit
                connection.commit();
                return true;

            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    // 선택한 결제 취소
    public boolean cancelPayment(
            int memberCode,
            int payCode
    ) throws SQLException {

        if (memberCode <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        if (payCode <= 0) {
            throw new IllegalArgumentException(
                    "결제번호는 1 이상이어야 합니다."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                PaymentDTO payment =
                        paymentDAO.findByPayCode(
                                connection, payCode
                        );

                // 해당 결제가 존재하지 않으면 취소 실패
                if (payment == null) {
                    connection.rollback();
                    return false;
                }

                // 로그인한 회원 본인의 결제가 아니면 취소 실패
                if (payment.getMemberCode() != memberCode) {
                    connection.rollback();
                    return false;
                }

                // 완료된 결제만 취소할 수 있다.
                if (!"COMPLETED".equals(
                        payment.getPaymentStatus()
                )) {
                    connection.rollback();
                    return false;
                }

                int result =
                        paymentDAO.cancelPayment(
                                connection, payCode, memberCode
                        );

                if (result != 1) {
                    connection.rollback();
                    return false;
                }

                // 취소 전에 결제 당시 적립된 포인트 계산
                int earnedPoint = memberService.calculateEarnedPoint(connection, memberCode, payment.getFinalAmount());

                // 실제 결제금액이 있을 시 누적 구매금액 차감
                if (payment.getFinalAmount() > 0) {

                    boolean amountReduced = memberService.minusTotalAmount(
                            connection, memberCode, payment.getFinalAmount());

                    if (!amountReduced) {
                        connection.rollback();
                        return false;
                    }
                }

                // 결제 시 사용했던 포인트 복구
                boolean pointRestored = memberService.restoreUsedPoint(connection, memberCode, payment.getPointUse());

                if (!pointRestored) {
                    connection.rollback();
                    return false;
                }

                // 결제로 적립됐던 포인트 회수
                boolean earnedPointCanceled = memberService.cancelEarnedPoint(connection, memberCode, earnedPoint);

                if (!earnedPointCanceled) {
                    connection.rollback();
                    return false;
                }

                // 결제가 취소된 주문을 PAID에서 CANCELED로 변경
                int orderStatusResult =
                        orderDAO.updateOrderStatusToCanceled(
                                connection,
                                payment.getOrderCode(),
                                memberCode
                        );

                if (orderStatusResult != 1) {
                    connection.rollback();
                    return false;
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    // 결제 등록값 검사
    private void validatePayment(PaymentDTO payment) {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "결제 정보가 없습니다."
            );
        }

        if (payment.getOrderCode() <= 0) {
            throw new IllegalArgumentException(
                    "주문번호는 1 이상이어야 합니다."
            );
        }

        if (payment.getMemberCode() <= 0) {
            throw new IllegalArgumentException(
                    "회원번호는 1 이상이어야 합니다."
            );
        }

        if (payment.getPaymentMethod() == null
                || payment.getPaymentMethod().isBlank()) {

            throw new IllegalArgumentException(
                    "결제 방식을 선택해야 합니다."
            );
        }

        if (payment.getOriginalAmount() < 0) {
            throw new IllegalArgumentException(
                    "할인 전 금액은 0원 이상이어야 합니다."
            );
        }

        if (payment.getDiscountAmount() < 0) {
            throw new IllegalArgumentException(
                    "할인 금액은 0원 이상이어야 합니다."
            );
        }

        if (payment.getPointUse() < 0) {
            throw new IllegalArgumentException(
                    "사용 포인트는 0 이상이어야 합니다."
            );
        }

        if (payment.getDiscountAmount() > payment.getOriginalAmount()) {
            throw new IllegalArgumentException(
                    "할인 금액은 상품 총액보다 클 수 없습니다."
            );
        }

        if (payment.getPointUse()
                > payment.getOriginalAmount() - payment.getDiscountAmount()) {
            throw new IllegalArgumentException(
                    "사용 포인트가 결제 예정 금액보다 큽니다."
            );
        }

        if (payment.getFinalAmount() < 0) {
            throw new IllegalArgumentException(
                    "최종 결제금액은 0원 이상이어야 합니다."
            );
        }

        int expectedFinalAmount =
                payment.getOriginalAmount()
                        - payment.getDiscountAmount()
                        - payment.getPointUse();

        if (payment.getFinalAmount() != expectedFinalAmount) {
            throw new IllegalArgumentException(
                    "최종 결제금액 계산이 올바르지 않습니다."
            );
        }
    }
}
