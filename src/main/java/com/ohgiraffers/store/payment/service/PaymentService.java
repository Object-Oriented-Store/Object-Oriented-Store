package com.ohgiraffers.store.payment.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.member.service.MemberService;
import com.ohgiraffers.store.payment.model.PaymentDTO;
import com.ohgiraffers.store.payment.repository.PaymentDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final MemberService memberService;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.memberService = new MemberService();
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

                if(paymentResult != 1) {
                    connection.rollback();
                    return false;
                }

                // 포인트 사용 시 차감 검증
                int pointUsed = 0;

                if(payment.getPointUse())


                // 누적 금액 증가 및 등급 변경 성공 여부 검증
                boolean amountUpdated = memberService.plusTotalAmount(connection, payment.getMemberCode(), payment.getFinalAmount());

                if (!amountUpdated) {
                    connection.rollback();
                    return false;
                }

                // 포인트 적립 여부 검증
                boolean pointEarned = memberService.earnPoint(connection, payment.getMemberCode(), payment.getFinalAmount());

                if (!pointEarned) {
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

                if (result == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

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
