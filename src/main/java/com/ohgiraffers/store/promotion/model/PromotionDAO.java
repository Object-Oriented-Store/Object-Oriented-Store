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

    public ResultSet updatePromotion(Connection conn, int wc){
        String sql = prop.getProperty("UpdatePromotion");
        System.out.println("===================================");
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        {
            try (ResultSet rs = pstmt.executeQuery()) {
                pstmt.setString(1, pd.getPromotionName());
                pstmt.setString(2, pd.getPromotionColumn());
                pstmt.setInt(3, pd.getDiscountValue());
                pstmt.setString(4, pd.getPromotionStatus());
                pstmt.setInt(5, pd.getPromotionCode());
                return rs;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerPromotion(){
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
                System.out.println("행사명: " + rs.getString("promotion_name"));
                System.out.println("행사내용: " + rs.getString("promotion_column"));
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
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        {
            try (ResultSet rs = pstmt.executeQuery()) {
                pstmt.setInt(1, delcode);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}