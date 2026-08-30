package com.ohgiraffers.store.promotion.model;

import com.ohgiraffers.store.maincontroller.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class PromotionDAO {
    PromotionDTO pd = new PromotionDTO();
    Properties prop = new Properties();

    /* 설명. DAO 객체가 만들어질 때 쿼리를 담아둔 XML을 한 번만 읽어 들인다. */
    public PromotionDAO() {
        try {
            prop.loadFromXML(new FileInputStream("src/main/java/com/ohgiraffers/store/common/mapper/promotion-query.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePromotion(Connection conn, int wc, PromotionDTO pd) {
        String sql = prop.getProperty("UpdatePromotion");
        System.out.println("===================================");

        // try-with-resources로 PreparedStatement 자동 해제
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. 쿼리 실행 전에 위치 홀더(?)에 값 대입
            pstmt.setString(1, pd.getPromotionName());
            pstmt.setString(2, pd.getPromotionColumn());
            pstmt.setInt(3, pd.getDiscountValue());
            pstmt.setString(4, pd.getPromotionStatus());
            pstmt.setInt(5, wc);

            // 2. UPDATE, INSERT, DELETE 문은 executeUpdate() 호출 (반환값: 수정된 행 수)
            pstmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("프로모션 수정 중 에러 발생", e);
        }
    }

    public void registerPromotion(PromotionDTO pd) {
        String query = prop.getProperty("RegisterPromotion");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
             PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, pd.getPromotionName());
            pstmt.setString(2, pd.getPromotionColumn());
            pstmt.setInt(3, pd.getDiscountValue());
            pstmt.setString(4, pd.getPromotionStatus());

            int result = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirmDelete(Connection conn, int wc){
        Controller st = new Controller();
        Scanner sc = new Scanner(System.in);
        String sql = prop.getProperty("Confirmdelete");
        System.out.println("===================================");
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        {
            try (ResultSet rs = pstmt.executeQuery()) {
                pstmt.setInt(1, wc);
                while (rs.next()) {
                    System.out.println("행사명: " + rs.getString("promotion_name"));
                    System.out.println("행사내용: " + rs.getString("promotion_column"));
                }
                System.out.println("=================================");
                System.out.println("위 행사를 삭제하시겠습니까? ");
                System.out.print("삭제를 원하시면 1, 아니라면 0을 입력하세요: ");
                int yesOrNot = sc.nextInt();
                if (yesOrNot == 1) deletePromotion(conn, wc);
                else st.startManager();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deletePromotion(Connection conn,int delcode){
        String sql = prop.getProperty("DeletePromotion");
        System.out.println("===================================");

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, delcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPromoWithProduct(Connection conn, PromotionDTO pd, int promoCode, int productCode) {
        String sql = prop.getProperty("RegisterPromotionProduct");
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, promoCode);
            pstmt.setInt(2, productCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}