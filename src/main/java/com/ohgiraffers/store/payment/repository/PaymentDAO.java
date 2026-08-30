package com.ohgiraffers.store.payment.repository;

import com.ohgiraffers.store.payment.model.PaymentDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public int insertPayment(Connection connection, PaymentDTO payment)
            throws SQLException {

        String query = """
                INSERT INTO tbl_payment (
                    order_code,
                    member_code,
                    payment_method,
                    original_amount,
                    discount_amount,
                    point_use,
                    final_amount,
                    payment_status
                    )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, payment.getOrderCode());
            pstmt.setInt(2, payment.getMemberCode());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setInt(4, payment.getOriginalAmount());
            pstmt.setInt(5, payment.getDiscountAmount());
            pstmt.setInt(6, payment.getPointUse());
            pstmt.setInt(7, payment.getFinalAmount());
            pstmt.setString(8, payment.getPaymentStatus());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        payment.setPayCode(
                                generatedKeys.getInt(1)
                        );
                    }
                }
            }

            return result;
        }
    }

    // 로그인한 회원의 전체 결제 내역 조회
    public List<PaymentDTO> findAllByMemberCode(Connection connection, int memberCode)
            throws SQLException {

        String query = """
                SELECT
                    pay_code,
                    order_code,
                    member_code,
                    payment_method,
                    original_amount,
                    discount_amount,
                    point_use,
                    final_amount,
                    payment_status
                FROM tbl_payment
                WHERE member_code = ?
                ORDER BY pay_code DESC
                """;

        List<PaymentDTO> payments = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, memberCode);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    PaymentDTO payment = new PaymentDTO(
                            rs.getInt("pay_code"),
                            rs.getInt("order_code"),
                            rs.getInt("member_code"),
                            rs.getString("payment_method"),
                            rs.getInt("original_amount"),
                            rs.getInt("discount_amount"),
                            rs.getInt("point_use"),
                            rs.getInt("final_amount"),
                            rs.getString("payment_status")
                    );

                    payments.add(payment);
                }
            }
        }

        return payments;
    }

    // 결제번호로 결제 한 건 조회
    public PaymentDTO findByPayCode(Connection connection, int payCode)
            throws SQLException {

        String query = """
                SELECT
                    pay_code,
                    order_code,
                    member_code,
                    payment_method,
                    original_amount,
                    discount_amount,
                    point_use,
                    final_amount,
                    payment_status
                FROM tbl_payment
                WHERE pay_code = ?
                """;

        PaymentDTO payment = null;

        try (PreparedStatement pstmt =
                    connection.prepareStatement(query)) {
            pstmt.setInt(1, payCode);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    payment = new PaymentDTO(
                            rs.getInt("pay_code"),
                            rs.getInt("order_code"),
                            rs.getInt("member_code"),
                            rs.getString("payment_method"),
                            rs.getInt("original_amount"),
                            rs.getInt("discount_amount"),
                            rs.getInt("point_use"),
                            rs.getInt("final_amount"),
                            rs.getString("payment_status")
                    );
                }
            }
        }

        return payment;
    }

    // 완료된 결제를 취소 상태로 변경
    public int cancelPayment(Connection connection, int payCode, int memberCode)
            throws SQLException {

        String query = """
                UPDATE tbl_payment
                SET payment_status = 'CANCELED'
                WHERE pay_code = ?
                    AND member_code = ?
                    AND payment_status = 'COMPLETED'
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, payCode);
            pstmt.setInt(2, memberCode);

            return pstmt.executeUpdate();
        }
    }

}
