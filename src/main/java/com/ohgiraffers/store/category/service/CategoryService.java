package com.ohgiraffers.store.category.service;

import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.category.repository.CategoryDAO;

import java.sql.SQLException;
import java.util.List;

/** 카테고리 조회 업무를 DAO에 요청하는 서비스 계층이다. */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public List<CategoryDTO> findAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }
}
