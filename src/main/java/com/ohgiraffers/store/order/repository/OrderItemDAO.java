package com.ohgiraffers.store.order.repository;

import com.ohgiraffers.store.order.model.OrderItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItemDAO {

    public int insertOrderItem(Connection connection, OrderItemDTO orderItem)
            throws SQLException {
        String query = """
            INSERT INTO tbl_order_item (
                order_code,
                product_code,
                quantity
            )
            VALUES (?, ?, ?)
            """;

        int result = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, orderItem.getOrderCode());
            pstmt.setInt(2, orderItem.getProductCode());
            pstmt.setInt(3, orderItem.getQuantity());

            result = pstmt.executeUpdate();
        }

        return result;
    }
}
