package com.ohgiraffers.store.promotion.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.promotion.model.PromotionDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class PromotionService {
    private final PromotionDAO promotionDAO = new PromotionDAO();

    public void printCurrentlyPromotion() {
        try (Connection con = DBConnection.getConnection()) {
            promotionDAO.printCurrentlyPromotion(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deletePromotion(int promotionCode) {
        try (Connection conn = DBConnection.getConnection()) {
            int result = promotionDAO.deletePromotion(conn, promotionCode);
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerPromotionProduct(int promotionCode, int productCode) {
        try (Connection conn = DBConnection.getConnection()) {
            int result = promotionDAO.registerPromotionProduct(
                    conn, promotionCode, productCode
            );

            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




}
