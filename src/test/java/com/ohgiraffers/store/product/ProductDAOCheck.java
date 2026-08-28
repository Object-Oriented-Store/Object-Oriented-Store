package com.ohgiraffers.store.product;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.product.model.ProductDTO;
import com.ohgiraffers.store.product.repository.ProductDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class ProductDAOCheck {

    private ProductDAOCheck() {
    }

    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAO();

        try {
            List<ProductDTO> products = productDAO.findAll();

            System.out.println("조회된 상품 수: " + products.size());
            products.forEach(System.out::println);

            System.out.println("\n상품코드 1 조회");
            System.out.println(productDAO.findByCode(1));

            System.out.println("\n카테고리코드 2 조회");
            productDAO.findByCategoryCode(2).forEach(System.out::println);

            System.out.println("\n상품명 '라면' 검색");
            productDAO.searchByName("라면").forEach(System.out::println);

            checkProductWrites(productDAO);
            System.out.println("롤백 후 상품 수: " + productDAO.findAll().size());
        } catch (SQLException exception) {
            System.err.println("상품 조회 실패: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static void checkProductWrites(ProductDAO productDAO) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                ProductDTO product = new ProductDTO(
                        "상품 DAO 테스트",
                        1000,
                        10,
                        2
                );

                int insertResult = productDAO.insertProduct(connection, product);
                System.out.println("\n상품 등록 결과: " + insertResult);
                System.out.println("생성된 상품코드: " + product.getProductCode());

                product.setProductName("수정된 상품 DAO 테스트");
                product.setProductPrice(1500);
                product.setStockQuantity(0);
                int updateResult = productDAO.updateProduct(connection, product);
                System.out.println("상품 수정 결과: " + updateResult);
                System.out.println("재고 0개 상품 상태 확인: "
                        + productDAO.findByCode(connection, product.getProductCode()));
            } finally {
                connection.rollback();
                System.out.println("테스트 데이터 롤백 완료");
            }
        }
    }
}
