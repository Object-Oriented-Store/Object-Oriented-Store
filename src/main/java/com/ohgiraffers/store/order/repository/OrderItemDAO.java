package com.ohgiraffers.store.order.repository;

import com.ohgiraffers.store.order.model.OrderItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItemDAO {

    // 1. 주문 상품 추가 또는 기존 상품 수량 증가
    public int insertOrderItem(Connection connection, OrderItemDTO orderItem)
            throws SQLException {
        String query = """
            INSERT INTO tbl_order_item (
                order_code,
                product_code,
                quantity
            )
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                quantity = quantity + ?
            """;

        int result = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, orderItem.getOrderCode());
            pstmt.setInt(2, orderItem.getProductCode());
            pstmt.setInt(3, orderItem.getQuantity());
            pstmt.setInt(4, orderItem.getQuantity());

            result = pstmt.executeUpdate();
        }

        return result;
    }

    // 2. 주문 상품 수량 수정
    public int updateOrderItemQuantity(
            Connection connection, OrderItemDTO orderItem
    ) throws SQLException {

        String query = """
                UPDATE tbl_order_item
                SET quantity = ?
                WHERE order_code = ?
                    AND product_code = ?
                """;

        int result = 0;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderItem.getQuantity());
            pstmt.setInt(2, orderItem.getOrderCode());
            pstmt.setInt(3, orderItem.getProductCode());

            result = pstmt.executeUpdate();
        }

        return result;
    }

    // 3. 선택한 주문 상품 삭제
    public int deleteOrderItem(
            Connection connection,
            int orderCode,
            int productCode
    ) throws SQLException {

        String query = """
                DELETE FROM tbl_order_item
                WHERE order_code = ?
                    AND product_code = ?
                """;

        int result = 0;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderCode);
            pstmt.setInt(2, productCode);

            result = pstmt.executeUpdate();
        }

        return result;
    }

    // 4. 주문에 추가된 상품 전체 삭제
    public int deleteAllOrderItems(
            Connection connection,
            int orderCode
    ) throws SQLException {

        String query = """
                DELETE FROM tbl_order_item
                WHERE order_code = ?
                """;

        int result = 0;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderCode);

            result = pstmt.executeUpdate();
        }

        return result;
    }
}
