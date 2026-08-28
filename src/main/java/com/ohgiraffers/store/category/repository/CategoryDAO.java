package com.ohgiraffers.store.category.repository;

import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.common.config.DBConnection;
import com.ohgiraffers.store.common.config.QueryProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** 카테고리 테이블에 조회 SQL을 실행하는 데이터 접근 객체이다. */
public class CategoryDAO {

    private static final String QUERY_FILE =
            "com/ohgiraffers/store/category/mapper/category-query.xml";
    private static final Properties QUERIES = QueryProvider.loadQueries(QUERY_FILE);

    private static final String SELECT_ALL_CATEGORIES =
            QueryProvider.getQuery(QUERIES, "category.selectAll");

    public List<CategoryDTO> findAll() throws SQLException {
        List<CategoryDTO> categories = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CATEGORIES);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(new CategoryDTO(
                        resultSet.getInt("category_code"),
                        resultSet.getString("category_name")
                ));
            }
        }

        return categories;
    }
}
