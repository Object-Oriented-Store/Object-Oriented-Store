package com.ohgiraffers.store.product.service;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.product.model.ProductDTO;
import com.ohgiraffers.store.product.repository.ProductDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 상품과 관련된 업무 규칙을 처리하는 계층이다.
 *
 * DAO는 SQL 실행만 담당하고, Service는 다음과 같은 판단을 담당한다.
 * 1. 사용자가 입력한 값이 올바른지 검사한다.
 * 2. 여러 DB 작업을 하나의 트랜잭션으로 관리한다.
 * 3. 작업 결과를 Controller에 반환한다.
 */
public class ProductService {

    /* 실제 SQL을 실행할 DAO를 Service가 가지고 있다. */
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    /** 등록된 모든 상품을 상품코드 순서로 조회한다. */
    public List<ProductDTO> findAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    /** 상품코드에 해당하는 상품 한 개를 조회한다. 없으면 null을 반환한다. */
    public ProductDTO findProductByCode(int productCode) throws SQLException {
        validateProductCode(productCode);
        return productDAO.findByCode(productCode);
    }

    /** 선택한 카테고리에 포함된 상품들을 조회한다. */
    public List<ProductDTO> findProductsByCategory(int categoryCode) throws SQLException {
        if (categoryCode <= 0) {
            throw new IllegalArgumentException("카테고리 코드는 1 이상이어야 합니다.");
        }

        return productDAO.findByCategoryCode(categoryCode);
    }

    /** 상품명의 일부를 입력받아 포함 검색을 수행한다. */
    public List<ProductDTO> searchProductsByName(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해야 합니다.");
        }

        return productDAO.searchByName(keyword.trim());
    }

    /**
     * 주문에 상품을 담기 직전 상품이 실제로 구매 가능한지 검사한다.
     * 판매 가능 여부는 재고로 결정되므로 요청 수량보다 재고가 적으면 구매를 차단한다.
     *
     * 실제 주문을 저장할 때는 재고가 동시에 바뀔 수 있으므로 OrderService의
     * 트랜잭션 안에서도 같은 검사를 다시 수행해야 한다.
     */
    public ProductDTO validateProductPurchase(int productCode, int quantity)
            throws SQLException {
        validateProductCode(productCode);

        if (quantity <= 0) {
            throw new IllegalArgumentException("구매수량은 1개 이상이어야 합니다.");
        }

        ProductDTO product = productDAO.findByCode(productCode);

        if (product == null) {
            throw new IllegalStateException("존재하지 않는 상품이라 구매할 수 없습니다.");
        }

        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "재고가 부족합니다. 현재 재고: " + product.getStockQuantity() + "개"
            );
        }

        return product;
    }

    /**
     * 상품을 새로 등록한다.
     *
     * autoCommit을 끈 뒤 성공하면 commit, 실패하면 rollback한다.
     * 등록 성공 시 DAO가 DB에서 생성된 상품코드를 ProductDTO에 넣어준다.
     */
    public boolean registerProduct(ProductDTO product) throws SQLException {
        validateProductForRegistration(product);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int affectedRows = productDAO.insertProduct(connection, product);

                if (affectedRows == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    /** 기존 상품의 이름, 가격, 재고, 카테고리를 수정한다. */
    public boolean updateProduct(ProductDTO product) throws SQLException {
        validateProductForUpdate(product);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                /* 없는 상품을 UPDATE했을 때 조용히 0행으로 끝나는 것을 방지한다. */
                if (productDAO.findByCode(connection, product.getProductCode()) == null) {
                    connection.rollback();
                    return false;
                }

                int affectedRows = productDAO.updateProduct(connection, product);

                if (affectedRows == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    /** 진행 중인 주문에 담기지 않은 상품을 논리 삭제한다. */
    public boolean deleteProduct(int productCode) throws SQLException {
        validateProductCode(productCode);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (productDAO.findByCode(connection, productCode) == null) {
                    connection.rollback();
                    return false;
                }

                int affectedRows =
                        productDAO.deleteProduct(
                                connection,
                                productCode
                        );

                if (affectedRows == 1) {
                    connection.commit();
                    return true;
                }

                connection.rollback();
                return false;

            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void validateProductForRegistration(ProductDTO product) {
        validateCommonProductFields(product);
    }

    private void validateProductForUpdate(ProductDTO product) {
        validateCommonProductFields(product);
        validateProductCode(product.getProductCode());
    }

    /** 등록과 수정에 공통으로 필요한 상품값을 검사하고 상품명의 공백을 정리한다. */
    private void validateCommonProductFields(ProductDTO product) {
        if (product == null) {
            throw new IllegalArgumentException("상품 정보가 없습니다.");
        }

        if (product.getProductName() == null || product.getProductName().isBlank()) {
            throw new IllegalArgumentException("상품명을 입력해야 합니다.");
        }
        product.setProductName(product.getProductName().trim());

        if (product.getProductPrice() < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }

        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("재고 수량은 0개 이상이어야 합니다.");
        }

        if (product.getCategoryCode() <= 0) {
            throw new IllegalArgumentException("카테고리 코드는 1 이상이어야 합니다.");
        }
    }

    private void validateProductCode(int productCode) {
        if (productCode <= 0) {
            throw new IllegalArgumentException("상품코드는 1 이상이어야 합니다.");
        }
    }

}
