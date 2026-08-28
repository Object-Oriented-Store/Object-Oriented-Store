package com.ohgiraffers.store.product.repository;

import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.common.config.QueryProvider;
import com.ohgiraffers.store.product.model.ProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 상품 테이블에 SQL을 실행하는 데이터 접근 계층이다.
 *
 * DAO(Data Access Object)는 SQL 실행과 ResultSet 변환에만 집중한다.
 * 입력값 검증, commit, rollback 같은 업무 판단은 ProductService가 담당한다.
 */
public class ProductDAO {

    private static final String QUERY_FILE =
            "com/ohgiraffers/store/product/mapper/product-query.xml";
    private static final Properties QUERIES = QueryProvider.loadQueries(QUERY_FILE);

    /* SQL 내용은 query.xml에 두고 DAO에는 XML에서 찾을 쿼리 이름만 둔다. */
    private static final String SELECT_ALL_PRODUCTS =
            QueryProvider.getQuery(QUERIES, "product.selectAll");
    private static final String SELECT_PRODUCT_BY_CODE =
            QueryProvider.getQuery(QUERIES, "product.selectByCode");
    private static final String SELECT_PRODUCTS_BY_CATEGORY =
            QueryProvider.getQuery(QUERIES, "product.selectByCategoryCode");
    private static final String SEARCH_PRODUCTS_BY_NAME =
            QueryProvider.getQuery(QUERIES, "product.searchByName");
    private static final String INSERT_PRODUCT =
            QueryProvider.getQuery(QUERIES, "product.insert");
    private static final String UPDATE_PRODUCT =
            QueryProvider.getQuery(QUERIES, "product.update");

    /** 상품 전체 조회는 단독 조회이므로 이 메서드 안에서 연결을 열고 자동으로 닫는다. */
    public List<ProductDTO> findAll() throws SQLException {
        List<ProductDTO> products = new ArrayList<>();

        /*
         * try-with-resources에 선언한 JDBC 자원은 블록이 끝날 때 역순으로 닫힌다.
         * ResultSet -> PreparedStatement -> Connection 순서로 자동 종료된다.
         */
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PRODUCTS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
        }

        return products;
    }

    /** 외부에서 상품 한 건을 조회할 때 사용할 편의 메서드이다. */
    public ProductDTO findByCode(int productCode) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return findByCode(connection, productCode);
        }
    }

    /** Service의 트랜잭션에 참여할 수 있도록 이미 열린 Connection을 전달받는다. */
    public ProductDTO findByCode(Connection connection, int productCode) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCT_BY_CODE)) {

            statement.setInt(1, productCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
            }
        }

        return null;
    }

    /** 카테고리코드를 ?에 안전하게 넣고 해당 카테고리 상품을 조회한다. */
    public List<ProductDTO> findByCategoryCode(int categoryCode) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {

            statement.setInt(1, categoryCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }
        }

        return products;
    }

    /** 앞뒤에 %를 붙여 상품명에 검색어가 포함된 상품을 조회한다. */
    public List<ProductDTO> searchByName(String keyword) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_PRODUCTS_BY_NAME)) {

            statement.setString(1, "%" + keyword + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }
        }

        return products;
    }

    /**
     * 상품을 등록하고 DB가 AUTO_INCREMENT로 만든 상품코드를 DTO에 저장한다.
     * Connection을 닫거나 commit하지 않는 이유는 트랜잭션 소유자가 Service이기 때문이다.
     */
    public int insertProduct(Connection connection, ProductDTO product) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                INSERT_PRODUCT,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, product.getProductName());
            statement.setInt(2, product.getProductPrice());
            statement.setInt(3, product.getStockQuantity());
            statement.setInt(4, product.getCategoryCode());

            int affectedRows = statement.executeUpdate();

            /* INSERT에 성공한 경우 MySQL이 생성한 product_code를 가져온다. */
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductCode(generatedKeys.getInt(1));
                    }
                }
            }

            return affectedRows;
        }
    }

    /** 상품코드를 조건으로 이름, 가격, 재고, 카테고리를 수정한다. */
    public int updateProduct(Connection connection, ProductDTO product) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            statement.setString(1, product.getProductName());
            statement.setInt(2, product.getProductPrice());
            statement.setInt(3, product.getStockQuantity());
            statement.setInt(4, product.getCategoryCode());
            statement.setInt(5, product.getProductCode());

            return statement.executeUpdate();
        }
    }

    /** ResultSet의 현재 행을 ProductDTO 한 개로 변환하는 공통 메서드이다. */
    private ProductDTO mapProduct(ResultSet resultSet) throws SQLException {
        return new ProductDTO(
                resultSet.getInt("product_code"),
                resultSet.getString("product_name"),
                resultSet.getInt("product_price"),
                resultSet.getString("product_status"),
                resultSet.getInt("stock_quantity"),
                resultSet.getInt("category_code")
        );
    }
}
