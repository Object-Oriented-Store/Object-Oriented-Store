package com.ohgiraffers.store.promotion.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class PromotionDAO {
    private Properties prop = new Properties();

    /* 설명. DAO 객체가 만들어질 때 쿼리를 담아둔 XML을 한 번만 읽어 들인다. */
    public PromotionDAO() {

        try {
            prop.loadFromXML(new FileInputStream("src/main/java/com/ohgiraffers/store/common/mapper/promotion-query.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printCurrentlyPromotion(Connection conn) throws SQLException {
        String sql = prop.getProperty("PrintCurrentlyPromotion");

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("행사 제목: " + rs.getString("promotion_name"));
                System.out.println("행사 내용: " + rs.getString("promotion_column"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int deletePromotion(Connection conn, int promotionCode) throws SQLException {
        String sql = prop.getProperty("DeletePromotion");

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, promotionCode);  // 첫 번째 ? = 입력한 행사 코드
            return pstmt.executeUpdate();    // 삭제된 행 개수 반환
        }
    }

    public int registerPromotionProduct(
            Connection conn, int promotionCode, int productCode) throws SQLException {

        String sql = prop.getProperty("RegisterPromotionProduct");

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, promotionCode);
            pstmt.setInt(2, productCode);

            return pstmt.executeUpdate();
        }
    }


}
