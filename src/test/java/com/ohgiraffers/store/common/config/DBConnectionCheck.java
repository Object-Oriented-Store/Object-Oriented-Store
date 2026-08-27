package com.ohgiraffers.store.common.config;

import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnectionCheck {

    private DBConnectionCheck() {
    }

    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (!connection.isValid(2)) {
                throw new SQLException("MySQL 연결이 유효하지 않습니다.");
            }

            System.out.println("DB 연결 성공");
            System.out.println("Database: " + connection.getCatalog());
        } catch (SQLException exception) {
            System.err.println("DB 연결 실패: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
