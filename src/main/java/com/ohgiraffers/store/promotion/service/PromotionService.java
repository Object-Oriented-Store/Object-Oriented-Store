package com.ohgiraffers.store.promotion.service;

import com.mysql.cj.protocol.Resultset;
import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.promotion.model.PromotionDAO;
import com.ohgiraffers.store.promotion.model.PromotionDTO;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class PromotionService {
    PromotionDAO promotionDAO = new PromotionDAO();
    private Properties prop = new Properties();
    PromotionDTO pd = new PromotionDTO();
    Scanner sc = new Scanner(System.in);
    Connection conn;

    {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCurrentlyPromotion(Connection conn) throws SQLException {
        String sql = prop.getProperty("PrintCurrentlyPromotion");
        System.out.println("===================================");
        PreparedStatement pstmt = conn.prepareStatement(sql); {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (Objects.equals(rs.getString("promotion_status"), "Y")) {
                        System.out.println("행사코드: " + rs.getInt("promotion_code"));
                        System.out.println("행사명: " + rs.getString("promotion_name"));
                        System.out.println("행사내용: " + rs.getString("promotion_column"));
                        System.out.println("할인율: " + rs.getString("discount_value") + "%");
                        System.out.println();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ResultSet updatePromotion(int wc){
        System.out.println(wc + "번 행사 수정");
        System.out.print("수정 행사명: ");
        pd.setPromotionName(sc.nextLine());
        System.out.print("수정 행사내용: ");
        pd.setPromotionColumn(sc.nextLine());
        System.out.print("수정 할인율: ");
        pd.setDiscountValue(sc.nextInt());

        ResultSet rs = promotionDAO.updatePromotion(conn, wc);

        System.out.println("==============수정된 행사 내용==============");
        System.out.println("행사명: " + pd.getPromotionName());
        System.out.println("행사내용: " + pd.getPromotionColumn());
        System.out.println("할인율: " + pd.getDiscountValue());
        System.out.println("행사 진행상태: " + pd.getPromotionStatus());
        System.out.println();
        return rs;
    }

    public void deletePromotion(Connection conn, int wc){
        promotionDAO.deletePromotion(conn, wc);
    }




}
