package com.ohgiraffers.store.promotion.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.promotion.model.PromotionDAO;
import com.ohgiraffers.store.promotion.model.PromotionDTO;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class SettingsOnlyManager {
    Scanner sc;

    Connection conn;

    {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/object_oriented_store", "oodbms", "oodbms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final Properties prop = new Properties();

    public SettingsOnlyManager(Scanner sc) {
        this.sc = sc;

        try {
            prop.loadFromXML(new FileInputStream(
                    "src/main/java/com/ohgiraffers/store/common/mapper/promotion-query.xml"
            ));
        } catch (IOException e) {
            throw new RuntimeException("쿼리 XML을 읽을 수 없습니다.", e);
        }
    }

    public SettingsOnlyManager() {

    }



    public void RegisterPromotion() {
        PromotionDAO pa = new PromotionDAO();
        PromotionService promotionService = new PromotionService();
        PromotionDTO pd = new PromotionDTO();
        System.out.println("============행사등록============");
        System.out.print("행사명: ");
        // String promotionName = sc.nextLine();
        pd.setPromotionName(sc.nextLine());
        System.out.print("행사내용: ");
        // String promotionColumn =  sc.nextLine();
        pd.setPromotionColumn(sc.nextLine());
        System.out.print("할인율: ");
        // int discount = Integer.parseInt(sc.nextLine());
        pd.setDiscountValue(sc.nextInt());
        pd.setPromotionStatus("Y");

        pa.registerPromotion();

        System.out.println("==========새로 등록한 행사==========");
        System.out.println("행사명: " + pd.getPromotionName());
        System.out.println("행사내용:  " + pd.getPromotionColumn());
        System.out.println("할인율: " + pd.getDiscountValue() + "%");
        System.out.println("행사의 상태: " + pd.getPromotionStatus());
        System.out.println("====================================");
    }

        public void RegisterPromotionProduct() throws SQLException {
        PromotionService promotionService = new PromotionService();
        promotionService.printCurrentlyPromotion(conn);

        System.out.println("상품을 등록할 행사코드을 입력하세요: ");
        int promotionCode = sc.nextInt();
        System.out.println("행사에 등록할 상품코드를 입력하세요");
        int promotionProductCode = sc.nextInt();


    }

    public void UpdatePromotion() throws SQLException {
        PromotionDTO pd = new PromotionDTO();
        PromotionService ps = new PromotionService();
        PromotionService promotionService = new PromotionService();
        PromotionDAO promotionDAO = new PromotionDAO();

        System.out.print("기존의 행사 목록을 조회하시겠습니까?");
        System.out.print("(1-Yes, 이외의 키-No): ");
        int WannaRead = sc.nextInt();
        if (WannaRead == 1) {
            ps.printCurrentlyPromotion(conn);
        }
        System.out.println("===================================");
        System.out.print("수정할 행사의 행사코드를 입력하세요: ");
        int WannaCode =  sc.nextInt();

        ResultSet rs = ps.updatePromotion(WannaCode);
        while (rs.next()) {
            System.out.println("수정 후 행사명: " + rs.getString("promotion_name"));
            System.out.println("수정 후 행사내용: " + rs.getString("promotion_column"));
            System.out.println("수정 후 할인율: " + rs.getString("discount_value") + "%");
            System.out.println();
        }

    }

    public void DeletePromotion(){
        PromotionDTO pd = new PromotionDTO();
        PromotionService ps = new PromotionService();
        PromotionDAO promotionDAO = new PromotionDAO();

        System.out.print("기존의 행사 목록을 조회하시겠습니까?(1-Yes, 이외의 키-No)");
        int WannaRead = sc.nextInt();
        if (WannaRead == 1) {
            ps.printCurrentlyPromotion(conn);

        }
        System.out.println("================================");
        System.out.println("삭제할 행사의 행사코드를 입력하세요: ");
        int delCode = sc.nextInt();
        ps.deletePromotion(conn,delCode);

        System.out.println(delCode + "번 행사의 데이터가 삭제되었습니다. ");

    }
}
