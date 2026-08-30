package com.ohgiraffers.store.promotion.service;

import com.mysql.cj.protocol.Resultset;
import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.promotion.model.PromotionDAO;
import com.ohgiraffers.store.promotion.model.PromotionDTO;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

import java.util.Properties;
import java.util.Scanner;

public class PromotionService {
    PromotionDAO promotionDAO = new PromotionDAO();
    private final Properties prop = new Properties();
    PromotionDTO pd = new PromotionDTO();
    Scanner sc = new Scanner(System.in);
    Connection conn;
    public PromotionService() {
        try {
            prop.loadFromXML(new FileInputStream(
                    "src/main/java/com/ohgiraffers/store/common/mapper/promotion-query.xml"
            ));
        } catch (IOException e) {
            throw new RuntimeException("프로모션 쿼리 XML 로드 실패", e);
        }
    }

    {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCurrentlyPromotion(Connection conn)  {

        String sql = prop.getProperty("PrintCurrentlyPromotion");
        System.out.println("===================================");

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("행사코드: " + rs.getInt("promotion_code"));
                System.out.println("행사명: " + rs.getString("promotion_name"));
                System.out.println("행사내용: " + rs.getString("promotion_column"));
                System.out.println("할인율: " + rs.getInt("discount_value") + "%");
                System.out.println("행사 진행상태: " +  rs.getString("promotion_status"));
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException("진행 중 행사 조회 실패", e);
        }

    }

    public void updatePromotion(int wc, PromotionDTO pd) {
        System.out.println("[" + wc + "번 행사 수정]");
        System.out.print("수정 행사명: ");
        pd.setPromotionName(sc.nextLine());
        System.out.print("수정 행사내용: ");
        pd.setPromotionColumn(sc.nextLine());
        System.out.print("수정 할인율: ");
        pd.setDiscountValue(sc.nextInt());
        sc.nextLine();
        System.out.print("수정할 행사의 진행상태: ");
        pd.setPromotionStatus(sc.nextLine());

        promotionDAO.updatePromotion(conn, wc, pd);

        System.out.println("수정한  행사명: " + pd.getPromotionName());
        System.out.println("수정한 행사내용: " + pd.getPromotionColumn());
        System.out.println("수정한 할인율: " + pd.getDiscountValue() + "%");
        System.out.println("수정한 행사의 진행상태: " + pd.getPromotionStatus());

    }

    public void deletePromotion(Connection conn, int wc){
        promotionDAO.deletePromotion(conn, wc);
    }




}
