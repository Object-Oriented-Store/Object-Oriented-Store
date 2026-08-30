package com.ohgiraffers.store.payment.controller;

import com.ohgiraffers.store.payment.model.PaymentDTO;
import com.ohgiraffers.store.payment.service.PaymentService;

import java.sql.SQLException;
import java.util.List;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
    }

    // 결제 등록 요청
    public boolean registerPayment(
            PaymentDTO payment
    ) throws SQLException {

        return paymentService.registerPayment(payment);
    }

    // 로그인한 회원의 전체 결제 내역 조회 요청
    public List<PaymentDTO> findAllPaymentsByMemberCode(
            int memberCode
    ) throws SQLException {

        return paymentService.findAllPaymentsByMemberCode(
                memberCode
        );
    }

    // 로그인한 회원의 결제 한 건 조회 요청
    public PaymentDTO findPaymentByPayCode(
            int memberCode,
            int payCode
    ) throws SQLException {

        return paymentService.findPaymentByPayCode(
                memberCode, payCode
        );
    }

    // 선택한 결제 취소 요청
    public boolean cancelPayment(
            int memberCode,
            int payCode
    ) throws SQLException {

        return paymentService.cancelPayment(
                memberCode, payCode
        );
    }
}
