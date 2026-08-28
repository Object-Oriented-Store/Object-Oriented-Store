package com.ohgiraffers.store.category.controller;

import com.ohgiraffers.store.category.model.CategoryDTO;
import com.ohgiraffers.store.category.service.CategoryService;

import java.sql.SQLException;
import java.util.List;

/** 메뉴의 카테고리 조회 요청을 Service에 전달하는 진입점이다. */
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController() {
        this.categoryService = new CategoryService();
    }

    public List<CategoryDTO> findAllCategories() throws SQLException {
        return categoryService.findAllCategories();
    }
}
