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

    // 숫자가 아닌 입력으로 프로그램이 종료되지 않도록 정수 입력을 반복 처리
    private int inputNumber(String prompt) {

        while (true) {
            System.out.print(prompt);

            // 한 줄 전체 입력 후 앞뒤 공백 제거
            String input = sc.nextLine().trim();

            try {
                // 문자열을 int로 변환
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력해주세요.");
            }
        }
    }

    public void RegisterPromotion() {
        PromotionDAO pa = new PromotionDAO();
        PromotionService promotionService = new PromotionService();
        PromotionDTO pd = new PromotionDTO();
        System.out.println("============행사등록============");
        System.out.print("행사명: ");
        pd.setPromotionName(sc.nextLine());
        System.out.print("행사내용: ");
        pd.setPromotionColumn(sc.nextLine());
        pd.setDiscountValue(inputNumber("할인율: "));
        pd.setPromotionStatus("Y");

        pa.registerPromotion(pd);

        System.out.println("==========새로 등록한 행사==========");
        System.out.println("행사명: " + pd.getPromotionName());
        System.out.println("행사내용:  " + pd.getPromotionColumn());
        System.out.println("할인율: " + pd.getDiscountValue() + "%");
        System.out.println("행사의 상태: " + pd.getPromotionStatus());
        System.out.println("====================================");
    }

        public void RegisterPromotionProduct() throws SQLException {
        PromotionService promotionService = new PromotionService();
        PromotionDAO pa = new PromotionDAO();
        PromotionService ps = new PromotionService();
        PromotionDTO pd = new PromotionDTO();
        promotionService.printCurrentlyPromotion(conn);

        int promotionCode = inputNumber("상품을 등록할 행사코드을 입력하세요: ");
        int promotionProductCode = inputNumber("행사에 등록할 상품코드를 입력하세요: ");

        pa.registerPromoWithProduct(conn, pd, promotionCode, promotionProductCode);

        System.out.println("상품의 행사등록이 완료되었습니다.");

    }

    public void UpdatePromotion() throws SQLException {
        PromotionService ps = new PromotionService();
        PromotionDTO pd = new PromotionDTO();

        int WannaRead = inputNumber("기존의 행사 목록을 조회하시겠습니까? \n"
                                                        + "(1-Yes, 이외의 키-No): ");
        if (WannaRead == 1) {
            ps.printCurrentlyPromotion(conn);
        }
        System.out.println("===================================");
        int WannaCode =  inputNumber("수정할 행사의 행사코드를 입력하세요: ");

        ps.updatePromotion(WannaCode, pd);

    }

    public void DeletePromotion(){
        PromotionDTO pd = new PromotionDTO();
        PromotionService ps = new PromotionService();
        PromotionDAO promotionDAO = new PromotionDAO();

        int WannaRead = inputNumber("기존의 행사 목록을 조회하시겠습니까?(1-Yes, 이외의 키-No)");
        if (WannaRead == 1) {
            ps.printCurrentlyPromotion(conn);

        }
        System.out.println("================================");
        int delCode = inputNumber("삭제할 행사의 행사코드를 입력하세요: ");
        ps.deletePromotion(conn,delCode);

        System.out.println(delCode + "번 행사의 데이터가 삭제되었습니다. ");

    }
}
