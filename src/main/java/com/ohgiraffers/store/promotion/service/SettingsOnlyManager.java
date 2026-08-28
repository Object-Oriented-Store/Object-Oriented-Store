package com.ohgiraffers.store.promotion.service;

import com.ohgiraffers.store.promotion.model.PromotionDAO;
import com.ohgiraffers.store.promotion.model.PromotionDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class SettingsOnlyManager {
    Scanner sc;

    public SettingsOnlyManager(Scanner sc) {
        this.sc = sc;
    }

    public SettingsOnlyManager() {

    }

    public void RegisterPromotion(){
        PromotionService promotionService = new PromotionService();

        promotionService.printCurrentlyPromotion();

        PromotionDTO pd = new PromotionDTO();

        System.out.print("행사명: ");
        pd.setPromotionName(sc.nextLine());
        System.out.print("행사내용: ");
        pd.setPromotionColumn(sc.nextLine());
        System.out.print("할인율: ");
        pd.setDiscountValue(sc.nextInt());
        pd.setPromotionStatus("Y");
    }

    public void RegisterPromotionProduct(){
        PromotionService promotionService = new PromotionService();
        promotionService.printCurrentlyPromotion();

        System.out.println("상품을 등록할 행사코드을 입력하세요: ");
        int promotionCode = sc.nextInt();
        System.out.println("행사에 등록할 상품코드를 입력하세요");
        int promotionProductCode = sc.nextInt();

        boolean isRegistered =
                promotionService.registerPromotionProduct(promotionCode, promotionProductCode);

        if (isRegistered) {
            System.out.println("행사 상품이 등록되었습니다.");
        } else {
            System.out.println("등록에 실패했습니다. 행사 코드와 상품 코드를 확인하세요.");
        }

    }

    public void UpdatePromotion(){
        PromotionDTO pd = new PromotionDTO();
        PromotionService ps = new PromotionService();
        PromotionService promotionService = new PromotionService();
        PromotionDAO promotionDAO = new PromotionDAO();

        Connection conn = null;

        System.out.print("기존의 행사 목록을 조회하시겠습니까?(1-Yes, 0-No)");
        int WannaRead = sc.nextInt();
        if (WannaRead == 1) {
            try {
                promotionDAO.printCurrentlyPromotion(conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.print("수정할 행사의 행사코드를 입력하세요: ");
        int WannaCode =  sc.nextInt();

    }

    public void DeletePromotion(){
        PromotionDTO pd = new PromotionDTO();
        PromotionService ps = new PromotionService();
        PromotionDAO promotionDAO = new PromotionDAO();

        System.out.print("기존의 행사 목록을 조회하시겠습니까?(1-Yes, 0-No)");
        int WannaRead = sc.nextInt();
        if (WannaRead == 1) {
            try {
                Connection conn = null;
                promotionDAO.printCurrentlyPromotion(conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.print("삭제할 행사의 행사코드를 입력하세요: ");
        int wannaCode =  sc.nextInt();

        boolean isDeleted = ps.deletePromotion(wannaCode);

        if (isDeleted) {
            System.out.println("행사가 삭제되었습니다.");
        } else {
            System.out.println("해당 행사코드가 없거나 삭제에 실패했습니다.");
        }
    }
}
