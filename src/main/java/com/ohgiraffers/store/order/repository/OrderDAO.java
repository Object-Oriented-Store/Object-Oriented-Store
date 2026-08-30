package com.ohgiraffers.store.order.repository;

import com.ohgiraffers.store.order.model.OrderDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class OrderDAO {

    // 1. 새 PENDING 주문 저장
    public int insertOrder(Connection connection, OrderDTO order)
            throws SQLException {

        String query = """
                INSERT INTO tbl_order (
                    order_code,
                    member_code,
                    original_amount,
                    discount_amount,
                    final_amount
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        int result = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, order.getOrderCode());
            pstmt.setInt(2, order.getMemberCode());
            pstmt.setInt(3, order.getOriginalAmount());
            pstmt.setInt(4, order.getDiscountAmount());
            pstmt.setInt(5, order.getFinalAmount());

            result = pstmt.executeUpdate();
        }

        return result;
    }

    // 2. 회원의 기존 PENDING 주문 조회
    public OrderDTO findPendingOrderByMemberCode(Connection connection, int memberCode)
            throws SQLException {

        String query = """
                    SELECT
                    order_code,
                    member_code,
                    original_amount,
                    discount_amount,
                    final_amount,
                    ordered_at,
                    order_status,
                    refunded_at
                FROM tbl_order
                WHERE member_code = ?
                    AND order_status = 'PENDING'
                ORDER BY ordered_at DESC
                LIMIT 1
                """;

        OrderDTO order = null;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, memberCode);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    order = new OrderDTO();

                    order.setOrderCode(rs.getInt("order_code"));
                    order.setMemberCode(rs.getInt("member_code"));
                    order.setOriginalAmount(rs.getInt("original_amount"));
                    order.setDiscountAmount(rs.getInt("discount_amount"));
                    order.setFinalAmount(rs.getInt("final_amount"));
                    order.setOrderedAt(
                            rs.getTimestamp("ordered_at").toLocalDateTime()
                    );
                    order.setOrderStatus(rs.getString("order_status"));

                    if (rs.getTimestamp("refunded_at") != null) {
                        order.setRefundedAt(
                                rs.getTimestamp("refunded_at").toLocalDateTime()
                        );
                    }
                }
            }
        }

        return order;
    }

    // 3. 주문 상품의 할인 전 총액 계산
    public int selectOriginalAmount(
            Connection connection,
            int orderCode
    ) throws SQLException {

        String query = """
                SELECT COALESCE(
                    SUM(p.product_price * oi.quantity),
                    0
                ) AS original_amount
                FROM tbl_order_item oi
                JOIN tbl_product p
                  ON p.product_code = oi.product_code
                WHERE oi.order_code = ?
                """;

        int originalAmount = 0;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    originalAmount =
                            rs.getInt("original_amount");
                }
            }
        }

        return originalAmount;
    }

    // 4. 계산한 주문 금액을 PENDING 주문에 저장
    public int updateOrderAmounts(
            Connection connection,
            OrderDTO order
    ) throws SQLException {

        String query = """
                UPDATE tbl_order
                SET original_amount = ?,
                    discount_amount = ?,
                    final_amount = ?
                WHERE order_code = ?
                  AND order_status = 'PENDING'
                """;

        int result = 0;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {
            pstmt.setInt(1, order.getOriginalAmount());
            pstmt.setInt(2, order.getDiscountAmount());
            pstmt.setInt(3, order.getFinalAmount());
            pstmt.setInt(4, order.getOrderCode());

            result = pstmt.executeUpdate();
        }

        return result;

    }

    // 5. 결제 완료 시 로그인 회원의 PENDING 주문을 PAID 상태로 변경
    public int updateOrderStatusToPaid(
            Connection connection,
            int orderCode,
            int memberCode
    ) throws SQLException {

        String query = """
                UPDATE tbl_order
                SET order_status = 'PAID'
                WHERE order_code = ?
                  AND member_code = ?
                  AND order_status = 'PENDING'
                """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            // 주문번호와 회원번호가 일치하는 PENDING 주문만 변경해
            // 다른 회원의 주문 변경 및 중복 결제를 방지한다.
            pstmt.setInt(1, orderCode);
            pstmt.setInt(2, memberCode);

            return pstmt.executeUpdate();
        }
    }

    // 6. 결제 취소 시 로그인 회원의 PAID 주문을 CANCELED 상태로 변경
    public int updateOrderStatusToCanceled(
            Connection connection,
            int orderCode,
            int memberCode
    ) throws SQLException {

        String query = """
                UPDATE tbl_order
                SET order_status = 'CANCELED',
                    refunded_at = CURRENT_TIMESTAMP
                WHERE order_code = ?
                    AND member_code = ?
                    AND order_status = 'PAID'
                    """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            // 결제가 완료된 본인의 주문만 취소하도록 제한한다.
            pstmt.setInt(1, orderCode);
            pstmt.setInt(2, memberCode);

            return pstmt.executeUpdate();
        }
    }
}
