package com.ohgiraffers.store.order.repository;

import com.ohgiraffers.store.order.model.OrderItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // 주문에 담긴 상품 전체 조회
    public List<OrderItemDTO> findAllByOrderCode(
            Connection connection,
            int orderCode
    ) throws SQLException {

        String query = """
            SELECT
                order_code,
                product_code,
                quantity
            FROM tbl_order_item
            WHERE order_code = ?
            ORDER BY product_code
            """;

        List<OrderItemDTO> orderItems =
                new ArrayList<>();

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderCode);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    OrderItemDTO orderItem =
                            new OrderItemDTO(
                                    rs.getInt("order_code"),
                                    rs.getInt("product_code"),
                                    rs.getInt("quantity")
                            );

                    orderItems.add(orderItem);
                }
            }
        }

        return orderItems;
    }

    // 주문에 이미 담긴 상품의 현재 수량을 조회한다.
    public int findQuantity(
            Connection connection,
            int orderCode,
            int productCode
    ) throws SQLException {

        String query = """
            SELECT quantity
            FROM tbl_order_item
            WHERE order_code = ?
              AND product_code = ?
            """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, orderCode);
            pstmt.setInt(2, productCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }

        return 0;
    }

    // 장바구니에 담는 수량만큼 상품 재고를 차감한다.
    // 현재 재고가 주문수량보다 적으면 수정하지 않고 0을 반환한다.
    public int decreaseProductStock(
            Connection connection,
            int productCode,
            int quantity
    ) throws SQLException {

        String query = """
            UPDATE tbl_product
            SET stock_quantity =
                    stock_quantity - ?
            WHERE product_code = ?
              AND stock_quantity >= ?
            """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productCode);
            pstmt.setInt(3, quantity);

            return pstmt.executeUpdate();
        }
    }

    // 장바구니 삭제 또는 결제 취소 시 수량만큼 상품 재고를 복구한다.
    public int increaseProductStock(
            Connection connection,
            int productCode,
            int quantity
    ) throws SQLException {

        String query = """
            UPDATE tbl_product
            SET stock_quantity =
                    stock_quantity + ?
            WHERE product_code = ?
            """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(query)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productCode);

            return pstmt.executeUpdate();
        }
    }
}
